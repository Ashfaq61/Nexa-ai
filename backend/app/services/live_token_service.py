import time
import secrets
from ..config import settings
from ..models.schemas import LiveTokenResponse

class LiveTokenService:
    @staticmethod
    def generate_ephemeral_token(session_id: str) -> LiveTokenResponse:
        """
        Creates a time-bound cryptographic token for client-to-Gemini-Live streaming.
        Never transmits the persistent GEMINI_API_KEY to the Android APK.
        """
        random_entropy = secrets.token_urlsafe(32)
        token = f"nexus_live_{int(time.time())}_{random_entropy}"
        
        # In production with Google Cloud Vertex/Gemini Live endpoint:
        # Client connects to wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent
        # or through backend WebSocket proxy:
        ws_endpoint = "wss://api.nexus.internal/v1/live/stream"
        
        return LiveTokenResponse(
            ephemeral_token=token,
            expires_in_seconds=3600,
            websocket_url=ws_endpoint
        )
