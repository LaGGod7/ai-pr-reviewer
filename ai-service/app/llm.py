from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_groq import ChatGroq

from app.config import settings

def get_llm():
    if settings.llm_provider == "gemini":
        return ChatGoogleGenerativeAI(
            model = settings.llm_model,
            google_api_key = settings.google_api_key,
            temperature = 0,
        )
    if settings.llm_provider == "groq":
        return ChatGroq(
            model=settings.llm_model,
            api_key=settings.groq_api_key,
            temperature=0,
        )

    raise ValueError(f"Unknown LLM provider: {settings.llm_provider}")