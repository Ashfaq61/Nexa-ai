# Project Nexus - Advanced Agentic Backend Architecture

This directory outlines the optional Python backend server for offloading heavy agentic workflows, long-term vector memory, and autonomous multi-agent orchestration.

## Overview

While the mobile application can query the Gemini API directly for low-latency voice and text responses, complex tasks (such as cloning repositories, running recursive code improvements in sandboxes, and executing multi-step search pipelines) are best served by a dedicated backend.

## Tech Stack

- **Framework**: FastAPI (Asynchronous Python 3.11+)
- **LLM Gateway**: Google Gemini 1.5 / 2.0 via `google-genai` SDK
- **Multi-Agent Frameworks**:
  - **CrewAI**: Multi-agent role-playing & delegation
  - **SmolAgents** (Hugging Face): Ultra-lightweight code agents executing Python actions
  - **LangGraph**: Stateful multi-actor orchestration
  - **Hermes Agent / OpenClaw**: Local autonomous reasoning
- **Self-Improvement & Reflection**:
  - **Reflexio & SIA (Self Improving AI)**: Iterative error-correction and agent telemetry reflection
  - **Self-Improving-Agent** (BerriAI pattern): Synthetic evaluation and safe candidate generation
- **Memory & Storage**:
  - PostgreSQL / Supabase with pgvector for semantic long-term memory
  - Redis for fast conversation caching and pub/sub streaming

## Architecture Diagram

```
[ Nexus Mobile App (Voice / Chat) ]
               │
               ▼ REST / WebSockets
   [ FastAPI Backend Gateway ]
        ├── Gemini API Proxy (Streaming Voice / Text)
        ├── GitHub & Hugging Face Intelligence Scrapers
        ├── Agent Orchestrator (CrewAI / SmolAgents / LangGraph)
        ├── Reflection & Self-Improvement Engine (Reflexio / SIA)
        └── Safe Proposal Generator (Human Review Gate)
```

## Security & Safety Guardrails

1. **Human-in-the-Loop Gate**: The backend NEVER automatically patches, compiles, or deploys mobile or backend code without an explicit cryptographic signature or confirmation token from the user.
2. **API Key Proxying**: Client apps authenticate with a single JWT; all Gemini, GitHub Personal Access Tokens, and Hugging Face keys remain strictly secured in backend environment variables.
3. **Containerized Sandboxing**: Autonomous agents running generated code execute only within ephemeral Docker containers with restricted network access.

## Quickstart

```bash
# Clone and enter backend
cd backend

# Create virtual environment
python3 -m venv venv
source venv/bin/activate

# Install requirements
pip install fastapi uvicorn google-genai httpx crewai smolagents pydantic

# Set environment keys
export GEMINI_API_KEY="your-gemini-api-key"
export GITHUB_TOKEN="optional-github-token"
export HF_TOKEN="optional-huggingface-token"

# Run server
uvicorn main:app --reload --port 8000
```
