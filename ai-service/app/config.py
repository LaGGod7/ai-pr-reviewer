from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    llm_provider: str = "gemini"
    llm_model: str
    google_api_key: str | None = None
    groq_api_key: str | None = None


settings = Settings()
