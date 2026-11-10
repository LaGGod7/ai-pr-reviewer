from langchain_core.prompts import (
    ChatPromptTemplate,
    MessagesPlaceholder,
    SystemMessagePromptTemplate,
    HumanMessagePromptTemplate,
)
import logging
from app.llm import get_llm
from app.models import ReviewRequest, ReviewResponse, ReviewResult
from app.exceptions import LLMUnavailableError
import re

MAX_DIFF_CHARS = 12_000

logger = logging.getLogger(__name__)

SYSTEM_PROMPT = """You are a senior software engineer reviewing a pull request.
You only see diff snippets, not full files. Do not comment on code you cannot see,
such as missing classes, imports or callers outside the snippet.

Each diff line starts with its line number in the NEW file. Use that number for `line`.
Lines starting with '-' were removed and have no number; do not report findings on them.

Focus on bugs, security issues and clear improvements.
Skip generic advice (like "add documentation" or "add tests") unless something specific is missing.
If the changes look good, return an empty findings list."""

prompt = ChatPromptTemplate.from_messages(
    [
        ("system", SYSTEM_PROMPT),
        ("human", "Pull request: {title}\n\nChanges:\n{diff}"),
    ]
)

HUNK_HEADER = re.compile(r"^@@ -\d+(?:,\d+)? \+(\d+)(?:,\d+)? @@")


def number_patch(patch: str) -> str:
    numbered = []
    new_line = 0
    for raw in patch.splitlines():
        header = HUNK_HEADER.match(raw)
        if header:
            new_line = int(header.group(1))
            numbered.append(raw)

        elif raw.startswith("-"):
            numbered.append(f"{'':>5} {raw}")
        elif raw.startswith("\\"):
            continue
        else:
            numbered.append(f"{new_line:>5} {raw}")
            new_line += 1

    return "\n".join(numbered)


def build_diff(request: ReviewRequest) -> tuple[str, int, bool]:
    parts = []
    total = 0
    truncated = False
    for file in request.files:
        if file.status == "REMOVED" or not file.patch:
            continue
        chunk = f"--{file.filename}\n{number_patch(file.patch)}\n"
        if total + len(chunk) > MAX_DIFF_CHARS:
            truncated = True
            break
        parts.append(chunk)
        total += len(chunk)
    return "".join(parts), len(parts), truncated


def build_review_chain():
    return prompt | get_llm().with_structured_output(ReviewResult)


def review_pull_request(request: ReviewRequest) -> ReviewResponse:
    diff, files_reviewed, truncated = build_diff(request)
    if files_reviewed == 0:
        return ReviewResponse(
            summary="No reviewable changes.", findings=[],files_reviewed=0, truncated=truncated
        )

    try:
        result = build_review_chain().invoke({"title": request.title, "diff": diff})
    except Exception as e:
        logger.exception("LLM invocation failed")
        raise LLMUnavailableError("The AI provider failed or timed out") from e

    return ReviewResponse(
        summary=result.summary,
        findings=result.findings,
        files_reviewed=files_reviewed,
        truncated=truncated,
    )
