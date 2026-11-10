

from app import review_service
from app.models import ReviewRequest, FileDiff,Finding, ReviewResult, Severity
from app.review_service import build_diff, review_pull_request,number_patch


class FakeChain:
    def __init__(self, result):
        self.result = result

    def invoke(self, _inputs):
        return self.result
    
def make_request(*files: FileDiff) -> ReviewRequest:
    return ReviewRequest(title="Test PR", files=list(files))

def test_build_diff_skips_removed_and_empty_patches():
    request = make_request(
        FileDiff(filename="A.java", status="MODIFIED", patch="+ok"),
        FileDiff(filename="Old.java", status="REMOVED", patch="-gone"),
        FileDiff(filename="logo.png", status="ADDED", patch=None),
    )
    diff, files_reviewed, truncated = build_diff(request)
    assert files_reviewed == 1
    assert "A.java" in diff
    assert "Old.java" not in diff
    assert truncated is False

def test_review_returns_structured_findings(monkeypatch):
    # Arrange: the answer our fake "AI" will give
    fake_result = ReviewResult(
        summary="One problem found",
        findings=[
            Finding(file="A.java", line=1, severity=Severity.MAJOR,
                    message="Bug here", suggestion=None)
        ],
    )
    monkeypatch.setattr(review_service, "build_review_chain", lambda: FakeChain(fake_result))
    
    # Act
    response = review_pull_request(
        make_request(FileDiff(filename="A.java", status="MODIFIED", patch="@@ -0,0 +1,1 @@\n+ok"))
    )
    # Assert
    assert response.summary == "One problem found"
    assert len(response.findings) == 1
    assert response.findings[0].severity == Severity.MAJOR
    assert response.files_reviewed == 1
        
    
# def test_review_returns_llm_text(monkeypatch):
#     fake_llm = FakeListChatModel(responses=["Looks good to me."])
#     monkeypatch.setattr(review_service, "get_llm", lambda: fake_llm)
    
#     response = review_pull_request(make_request(
#         FileDiff(filename="A.java", status="MODIFIED", patch="+ok"),
#     ))
#     assert response.review == "Looks good to me."
#     assert response.files_reviewed == 1
    
def test_review_with_nothing_does_not_call_llm(monkeypatch):
    def fail_if_called():
        raise RuntimeError("LLM should not be called")
    monkeypatch.setattr(review_service, "get_llm", fail_if_called)
    response = review_pull_request(make_request(
    FileDiff(filename="Old.java", status="REMOVED", patch="-gone")
    ))
    assert response.files_reviewed == 0
    
def test_build_diff_truncates_large_diffs():
    # Arrange: two big files, each about 7,000 characters
    request = make_request(
        FileDiff(filename="Big1.java", status="MODIFIED", patch="x" * 7000),
        FileDiff(filename="Big2.java", status="MODIFIED", patch="x" * 7000),
    )

    # Act
    diff, files_reviewed, truncated = build_diff(request)

    # Assert
    assert files_reviewed == 1
    assert truncated is True
    assert "Big1.java" in diff
    assert "Big2.java" not in diff

def test_number_patch_skips_removed_lines():
    patch = "@@ -1,2 +1,2 @@\n+new first\n-old second\n context"
    result = number_patch(patch).splitlines()

    assert result[1].strip() == "1 +new first"
    assert result[2].strip() == "-old second"
    assert result[3].strip() == "2  context"
