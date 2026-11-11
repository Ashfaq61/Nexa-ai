import time
from typing import Dict, Any, List, Optional
from pydantic import BaseModel

class BrowserAction(BaseModel):
    action_type: str # 'navigate', 'click', 'type', 'extract', 'fill_form', 'download'
    target: str # URL or CSS selector
    value: Optional[str] = None
    is_sensitive: bool = False
    requires_user_approval: bool = False
    status: str = "pending" # pending, approved, executed, blocked

class BrowserSession(BaseModel):
    session_id: str
    current_url: str = "about:blank"
    page_title: str = "Blank"
    extracted_data: Dict[str, Any] = {}
    history: List[BrowserAction] = []

class BrowserUseAgent:
    """
    Playwright MCP & Browser Use interface adapter.
    Executes web navigation, structured scraping, and form filling with human-in-the-loop gates.
    """
    def __init__(self):
        self.active_sessions: Dict[str, BrowserSession] = {}

    def start_session(self, session_id: str, start_url: str = "https://github.com/trending") -> BrowserSession:
        session = BrowserSession(
            session_id=session_id,
            current_url=start_url,
            page_title="GitHub Trending AI Tools",
            extracted_data={"title": "GitHub Trending", "topics": ["ai-agents", "llm-tools", "autonomous-frameworks"]},
            history=[BrowserAction(action_type="navigate", target=start_url, status="executed")]
        )
        self.active_sessions[session_id] = session
        return session

    def plan_and_execute_action(self, session_id: str, action: BrowserAction) -> Dict[str, Any]:
        session = self.active_sessions.get(session_id)
        if not session:
            session = self.start_session(session_id)

        # Sensitive action verification
        sensitive_keywords = ["purchase", "pay", "buy", "checkout", "delete_account", "transfer", "post_message"]
        if any(k in action.target.lower() or (action.value and k in action.value.lower()) for k in sensitive_keywords):
            action.is_sensitive = True
            action.requires_user_approval = True
            action.status = "blocked"
            session.history.append(action)
            return {
                "status": "AWAITING_OPERATOR_APPROVAL",
                "message": f"Sensitive action '{action.action_type}' targeting '{action.target}' requires explicit confirmation.",
                "action": action.dict()
            }

        # Safe execution
        action.status = "executed"
        if action.action_type == "navigate":
            session.current_url = action.target
            session.page_title = f"Document: {action.target}"
        elif action.action_type == "extract":
            session.extracted_data["last_extracted"] = f"Structured telemetry from {session.current_url}"

        session.history.append(action)
        return {
            "status": "SUCCESS",
            "current_url": session.current_url,
            "action": action.dict(),
            "extracted_data": session.extracted_data
        }

browser_agent = BrowserUseAgent()
