from fastapi import FastAPI
from app.llm import get_llm
from app.config import settings
app = FastAPI(title = "PR Reviewer AI Service")
from app.models import ReviewResponse,ReviewRequest
from app.review_service import review_pull_request

@app.get("/api/v1/health")
def health():
    return {"status": "UP","service": "pr-reviewer-ai-service"}

@app.post("/api/v1/review",response_model=ReviewResponse)
def review(request: ReviewRequest):
    return review_pull_request(request)