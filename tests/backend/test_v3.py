import pytest
from fastapi.testclient import TestClient
from backend.app.main import app

client = TestClient(app)

def test_v3_health():
    res = client.get("/health")
    assert res.status_code == 200
    data = res.json()
    assert data["version"] == "3.0.0"
    assert data["multi_model_routing"] == "ACTIVE"
    assert data["opensandbox_guard"] == "ENFORCED"

def test_model_router():
    # Test health of providers
    res = client.get("/v3/router/health")
    assert res.status_code == 200
    providers = res.json()
    assert "gemini" in providers
    assert "openrouter" in providers
    assert "huggingface" in providers

    # Test route request
    res2 = client.post("/v3/router/route", json={"task_type": "deep_reasoning"})
    assert res2.status_code == 200
    route = res2.json()
    assert route["provider_id"] == "gemini"
    assert "pro" in route["model_name"]

    # Test cost calculation
    res3 = client.get("/v3/router/cost")
    assert res3.status_code == 200
    assert "budget_limit_usd" in res3.json()

def test_task_engine_lifecycle():
    # 1. Plan task
    res = client.post("/v3/tasks/plan", json={"title": "Scout Hugging Face Spaces", "task_prompt": "Scout and summarize", "requires_approval": False})
    assert res.status_code == 200
    task = res.json()
    task_id = task["task_id"]
    assert len(task["subgoals"]) >= 3

    # 2. Step task
    res_step = client.post(f"/v3/tasks/{task_id}/step")
    assert res_step.status_code == 200
    updated = res_step.json()
    assert updated["current_step_index"] == 1

    # 3. Pause & Resume
    res_pause = client.post(f"/v3/tasks/{task_id}/pause")
    assert res_pause.status_code == 200
    assert res_pause.json()["status"] == "paused"

    res_resume = client.post(f"/v3/tasks/{task_id}/resume")
    assert res_resume.status_code == 200
    assert res_resume.json()["status"] == "running"

def test_hindsight_memory():
    # 1. Store memory with PII
    res = client.post("/v3/memory/store", json={"content": "Operator contact is admin@nexus.ai with key AIzaSyTestKey", "category": "preference", "tags": ["admin"]})
    assert res.status_code == 200
    mem = res.json()
    assert "[REDACTED_SENSITIVE]" in mem["content"] # Verifies privacy scrubbing

    # 2. Semantic search
    res_search = client.get("/v3/memory/semantic?q=safe-mode")
    assert res_search.status_code == 200
    assert len(res_search.json()) > 0

    # 3. Export backup
    res_exp = client.get("/v3/memory/export")
    assert res_exp.status_code == 200
    assert "hindsight-v1" in res_exp.json()["backup_json"]

def test_browser_agent_safety():
    # 1. Start browser session
    res = client.post("/v3/browser/session", json={"session_id": "test_browse", "start_url": "https://huggingface.co/models"})
    assert res.status_code == 200
    assert res.json()["session_id"] == "test_browse"

    # 2. Execute safe action
    res_nav = client.post("/v3/browser/action?session_id=test_browse", json={"action_type": "extract", "target": "h1"})
    assert res_nav.status_code == 200
    assert res_nav.json()["status"] == "SUCCESS"

    # 3. Intercept sensitive action
    res_sensitive = client.post("/v3/browser/action?session_id=test_browse", json={"action_type": "click", "target": "button#checkout_purchase"})
    assert res_sensitive.status_code == 200
    assert res_sensitive.json()["status"] == "AWAITING_OPERATOR_APPROVAL" # Human gate enforced

def test_sandbox_security_rules():
    # 1. Allowlisted package
    res_ok = client.post("/v3/sandbox/verify", json={"package_name": "smolagents", "code_snippet": "from smolagents import CodeAgent"})
    assert res_ok.status_code == 200
    assert res_ok.json()["status"] == "success"

    # 2. Unverified package blocked
    res_blocked = client.post("/v3/sandbox/verify", json={"package_name": "malicious_untrusted_pkg", "code_snippet": "import hack"})
    assert res_blocked.status_code == 200
    assert res_blocked.json()["status"] == "blocked"

    # 3. Dangerous syscall blocked
    res_danger = client.post("/v3/sandbox/verify", json={"package_name": "crewai", "code_snippet": "import os; os.system('rm -rf /')"})
    assert res_danger.status_code == 200
    assert res_danger.json()["status"] == "security_violation"

def test_observability_dashboard():
    res = client.get("/v3/observability/health")
    assert res.status_code == 200
    data = res.json()
    assert "feature_matrix" in data
    assert data["feature_matrix"]["model_router_fallback"] == "PASS"
    assert data["feature_matrix"]["opensandbox_isolation"] == "PASS"
