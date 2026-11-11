import pytest
from fastapi.testclient import TestClient
from backend.app.main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert "Nexus" in data["app"]

def test_chat_endpoint():
    response = client.post("/chat", json={"message": "Status check"})
    assert response.status_code == 200
    data = response.json()
    assert "reply" in data
    assert len(data["agent_trace"]) > 0

def test_live_token():
    response = client.post("/live/token", json={"session_id": "test_session"})
    assert response.status_code == 200
    data = response.json()
    assert "ephemeral_token" in data
    assert "websocket_url" in data

def test_agent_status():
    response = client.get("/agent/status")
    assert response.status_code == 200
    data = response.json()
    assert data["nexus_master_agent"] == "ONLINE"
    assert "security_agent" in data["sub_agents"]

def test_proposals_flow():
    response = client.get("/proposals")
    assert response.status_code == 200
    proposals = response.json()
    assert len(proposals) > 0
    first_id = proposals[0]["id"]
    approve_res = client.post(f"/proposals/{first_id}/approve", json={"approved": True})
    assert approve_res.status_code == 200
    assert approve_res.json()["is_approved"] is True
