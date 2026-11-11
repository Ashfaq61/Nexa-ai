import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    APP_NAME: str = "Nexus AI Assistant Backend"
    VERSION: str = "2.0.0"
    API_PREFIX: str = "/api/v1"
    GEMINI_API_KEY: str = os.getenv("GEMINI_API_KEY", "")
    DEFAULT_GEMINI_MODEL: str = os.getenv("DEFAULT_GEMINI_MODEL", "gemini-1.5-flash")
    GITHUB_TOKEN: str = os.getenv("GITHUB_TOKEN", "")
    HF_TOKEN: str = os.getenv("HF_TOKEN", "")
    SAFE_MODE: bool = True
    MAX_AGENT_STEPS: int = 8
    RATE_LIMIT_PER_MINUTE: int = 60

    class Config:
        env_file = ".env"
        extra = "ignore"

settings = Settings()
