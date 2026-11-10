package org.gd.backend.service;

import org.gd.backend.client.AiReviewClient;
import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.PullRequestReviewResponse;
import org.gd.backend.dto.ai.AiReviewRequest;
import org.gd.backend.dto.ai.AiReviewResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final GitHubService gitHubService;
    private final AiReviewClient aiReviewClient;

    public ReviewService(GitHubService gitHubService, AiReviewClient aiReviewClient) {
        this.gitHubService = gitHubService;
        this.aiReviewClient = aiReviewClient;
    }

    public PullRequestReviewResponse reviewPullRequest(String owner , String repo ,int number){
        PullRequestDetailsResponse pr = gitHubService.getPullRequestDetails(owner,repo,number);
        AiReviewResponse ai = aiReviewClient.review(toAiRequest(pr));

        List<PullRequestReviewResponse.Finding> findings = ai.findings().stream().map(this::toFinding).toList();
        return new PullRequestReviewResponse(pr.number(),pr.title(),pr.url(),ai.summary(),ai.filesReviewed(),ai.truncated(),findings);
    }
    private AiReviewRequest toAiRequest(PullRequestDetailsResponse pr){
        List<AiReviewRequest.FileDiff>files = pr.files().stream()
                .map(f->new AiReviewRequest.FileDiff(f.filename(),f.status().name(),f.patch()))
                .toList();
        return new AiReviewRequest(pr.title(),files);
    }

    private PullRequestReviewResponse.Finding toFinding(AiReviewResponse.Finding f){
        return new PullRequestReviewResponse.Finding(f.file(),f.line(),f.severity(),f.message(),f.suggestion());
    }


}
