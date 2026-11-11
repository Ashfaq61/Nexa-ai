from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime

class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1)
    session_id: Optional[str] = "default"
    language: Optional[str] = "en" # 'en', 'ur', 'roman_ur'
    model: Optional[str] = None

class ChatResponse(BaseModel):
    reply: str
    session_id: str
    tokens_used: Optional[int] = None
    agent_trace: Optional[List[str]] = None

class LiveTokenRequest(BaseModel):
    session_id: str = "default_live"

class LiveTokenResponse(BaseModel):
    ephemeral_token: str
    expires_in_seconds: int = 3600
    websocket_url: str

class ResearchRequest(BaseModel):
    query: str = "ai agent"
    limit: int = 8

class ToolItem(BaseModel):
    title: str
    source: str
    description: str
    url: str
    stars_or_downloads: int
    scouted_at: datetime = Field(default_factory=datetime.utcnow)

class AgentRunRequest(BaseModel):
    task: str
    require_approval: bool = True

class AgentStep(BaseModel):
    agent_name: str
    thought: str
    action: Optional[str] = None
    output: Optional[str] = None

class AgentRunResponse(BaseModel):
    task_id: str
    status: str # 'completed', 'awaiting_approval', 'failed'
    final_result: str
    steps: List[AgentStep]
    requires_user_approval: bool = False

class UpgradeProposalItem(BaseModel):
    id: str
    title: str
    category: str
    summary: str
    rationale: str
    risk_assessment: str
    markdown_diff: str
    is_approved: bool = False
    created_at: datetime = Field(default_factory=datetime.utcnow)

class ProposalApprovalRequest(BaseModel):
    approved: bool
    operator_token: Optional[str] = "operator_confirmed"
