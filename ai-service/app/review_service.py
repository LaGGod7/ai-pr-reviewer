from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder, SystemMessagePromptTemplate, HumanMessagePromptTemplate
import logging
from app.llm import get_llm
from app.models import ReviewRequest, ReviewResponse
from app.exceptions import LLMUnavailableError
MAX_DIFF_CHARS = 12_000

logger = logging.getLogger(__name__)

SYSTEM_PROMPT = """You are a senior software engineer reviewing a pull request.
You only see diff snippets, not full files. Do not comment on code you cannot see,
such as missing classes, imports or callers outside the snippet.
Focus on bugs, security issues and clear improvements.
Skip generic advice (like "add documentation" or "add tests") unless something specific is missing.
Be concise and specific, and mention the file name for every point.
If the changes look good, say so."""

prompt = ChatPromptTemplate.from_messages([
    ("system", SYSTEM_PROMPT),
    ("human","Pull request: {title}\n\nChanges:\n{diff}"),
])

def build_diff(request: ReviewRequest) -> tuple[str,int,bool]:
    parts = []
    total =0
    truncated = False
    for file in request.files:
        if file.status == "REMOVED" or not file.patch:
            continue
        chunk = f"--{file.filename}\n{file.patch}\n"
        if total + len(chunk) > MAX_DIFF_CHARS:
            truncated = True
            break
        parts.append(chunk)
        total += len(chunk)
    return "".join(parts), len(parts), truncated

def review_pull_request(request:ReviewRequest) -> ReviewResponse:
    diff,files_reviewed,truncated = build_diff(request)
    if files_reviewed == 0:
        return ReviewResponse(review="No reviewable changes.", files_reviewed=0, truncated=truncated)
    chain = prompt | get_llm()
    try:
        result = chain.invoke({"title": request.title, "diff": diff})
    except Exception as e:
        logger.exception("LLM invocation failed")
        raise LLMUnavailableError("The AI provider failed or timed out") from e
    
    return ReviewResponse(review=result.text, files_reviewed=files_reviewed, truncated=truncated)

     