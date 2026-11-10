from pydantic import BaseModel, Field
from enum import Enum


class Severity(str, Enum):
    CRITICAL = "CRITICAL"
    MAJOR = "MAJOR"
    MINOR = "MINOR"


class Finding(BaseModel):
    file: str = Field(
        description="Path of the file, exactly as shown in the diff header"
    )
    line: int | None = Field(
        description="Line number in new file ,or null if not tied to one line"
    )
    severity: Severity = Field(
        description=(
            "CRITICAL = security vulnerability (injection, hardcoded secrets, auth bypass) or crash; "
            "MAJOR = real bug that causes wrong behaviour; "
            "MINOR = small improvement or style"
        )
    )
    message: str = Field(description="What is wrong, in one or two sentences")
    suggestion: str | None = Field(description="How to fix it, or null if obvious")


class ReviewResult(BaseModel):
    summary: str = Field(
        description="One or two sentences summarizing the overall quality of the changes"
    )
    findings: list[Finding] = Field(
        description="List of specific issues found, if any else empty"
    )


class FileDiff(BaseModel):
    filename: str
    status: str
    patch: str | None = None


class ReviewRequest(BaseModel):
    title: str
    files: list[FileDiff] = Field(min_length=1)


class ReviewResponse(BaseModel):
    summary: str
    findings: list[Finding]
    files_reviewed: int
    truncated: bool
