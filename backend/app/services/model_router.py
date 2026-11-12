import time
import httpx
from typing import Dict, Any, List, Optional
from pydantic import BaseModel
from ..config import settings

class ModelProvider(BaseModel):
    id: str
    name: str
    base_url: str
    api_key_env: str
    is_available: bool = True
    input_cost_per_million: float
    output_cost_per_million: float
    latency_ms: int = 120
    is_free_tier: bool = False

class RouteDecision(BaseModel):
    provider_id: str
    model_name: str
    reason: str
    estimated_cost_usd: float
    priority_level: int

class ModelRouter:
    def __init__(self):
        self.providers: Dict[str, ModelProvider] = {
            "gemini": ModelProvider(
                id="gemini",
                name="Google Gemini API",
                base_url="https://generativelanguage.googleapis.com",
                api_key_env="GEMINI_API_KEY",
                is_available=True,
                input_cost_per_million=0.075,
                output_cost_per_million=0.30,
                latency_ms=110,
                is_free_tier=True # Generative Language free rate limits
            ),
            "openrouter": ModelProvider(
                id="openrouter",
                name="OpenRouter Unified Gateway",
                base_url="https://openrouter.ai/api/v1",
                api_key_env="OPENROUTER_API_KEY",
                is_available=True,
                input_cost_per_million=0.15,
                output_cost_per_million=0.60,
                latency_ms=190,
                is_free_tier=False
            ),
            "huggingface": ModelProvider(
                id="huggingface",
                name="Hugging Face Inference Providers",
                base_url="https://api-inference.huggingface.co",
                api_key_env="HF_TOKEN",
                is_available=True,
                input_cost_per_million=0.05,
                output_cost_per_million=0.20,
                latency_ms=210,
                is_free_tier=True # Rate-limited community tier
            ),
            "local_ollama": ModelProvider(
                id="local_ollama",
                name="Local Model Endpoint (Ollama/vLLM)",
                base_url="http://127.0.0.1:11434/v1",
                api_key_env="LOCAL_KEY",
                is_available=False, # Local endpoint requires active Ollama daemon
                input_cost_per_million=0.0,
                output_cost_per_million=0.0,
                latency_ms=65,
                is_free_tier=True
            )
        }
        self.total_tokens_consumed = 0
        self.total_cost_usd = 0.0
        self.max_budget_usd = 10.0 # Configurable user budget limit

    def check_health(self) -> Dict[str, Any]:
        return {
            provider_id: {
                "name": p.name,
                "available": p.is_available,
                "latency_ms": p.latency_ms,
                "free_tier": p.is_free_tier
            }
            for provider_id, p in self.providers.items()
        }

    def route_request(self, task_type: str, preferred_provider: Optional[str] = None) -> RouteDecision:
        # Check budget limit
        if self.total_cost_usd >= self.max_budget_usd:
            # Force fall back to local or free tier
            return RouteDecision(
                provider_id="gemini",
                model_name="gemini-1.5-flash",
                reason="Budget ceiling reached; enforcing lowest-cost free tier fallback.",
                estimated_cost_usd=0.0,
                priority_level=1
            )

        if preferred_provider and preferred_provider in self.providers:
            provider = self.providers[preferred_provider]
            if provider.is_available:
                return RouteDecision(
                    provider_id=provider.id,
                    model_name="gemini-1.5-pro" if provider.id == "gemini" else "meta-llama/llama-3.3-70b",
                    reason=f"User-selected provider: {provider.name}",
                    estimated_cost_usd=0.00012,
                    priority_level=1
                )

        # Automatic intelligent routing based on task complexity
        if task_type in ["code_generation", "deep_reasoning", "browser_plan"]:
            return RouteDecision(
                provider_id="gemini",
                model_name="gemini-1.5-pro",
                reason="Task requires advanced reasoning; routed to high-parameter Tier-1 model.",
                estimated_cost_usd=0.00025,
                priority_level=1
            )
        elif task_type in ["voice_chat", "quick_summary"]:
            return RouteDecision(
                provider_id="gemini",
                model_name="gemini-1.5-flash",
                reason="Low-latency conversational query; routed to lightweight flash model.",
                estimated_cost_usd=0.00003,
                priority_level=1
            )
        else:
            return RouteDecision(
                provider_id="huggingface",
                model_name="google/gemma-2-9b-it",
                reason="General query routed to Hugging Face Inference provider.",
                estimated_cost_usd=0.00005,
                priority_level=2
            )

    def record_usage(self, input_tokens: int, output_tokens: int, provider_id: str):
        self.total_tokens_consumed += (input_tokens + output_tokens)
        provider = self.providers.get(provider_id, self.providers["gemini"])
        cost = (input_tokens / 1_000_000.0 * provider.input_cost_per_million) + \
               (output_tokens / 1_000_000.0 * provider.output_cost_per_million)
        self.total_cost_usd += cost
        return cost

model_router = ModelRouter()
