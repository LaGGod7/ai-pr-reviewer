package org.gd.backend.service;

import org.gd.backend.client.GitHubClient;
import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.ai.AiReviewResponse;
import org.gd.backend.dto.github.GitHubCreateReviewRequest;
import org.gd.backend.dto.github.GitHubReviewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewPublisher {
    private static final Logger log = LoggerFactory.getLogger(ReviewPublisher.class);

    private final GitHubClient gitHubClient;
    public ReviewPublisher(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }
    private Map<String, Set<Integer>> commentableLinesByFile(PullRequestDetailsResponse pr) {
        Map<String, Set<Integer>> result = new HashMap<>();
        for (PullRequestDetailsResponse.ChangedFile file : pr.files()) {
            result.put(file.filename(), DiffLines.commentableLines(file.patch()));
        }
        return result;
    }
    private boolean isInDiff(AiReviewResponse.Finding finding,
                             Map<String, Set<Integer>> linesByFile) {
        if (finding.line() == null) {
            return false;
        }
        Set<Integer> lines = linesByFile.get(finding.file());
        if (lines == null) {
            return false;
        }
        return lines.contains(finding.line());
    }

    public GitHubReviewResponse publish(String owner, String repo, PullRequestDetailsResponse pr, AiReviewResponse ai){
        // 1. Build the lookup table
        Map<String, Set<Integer>> linesByFile = commentableLinesByFile(pr);

        // 2. Sort findings into two piles
        List<GitHubCreateReviewRequest.Comment> inline = new ArrayList<>();
        List<AiReviewResponse.Finding>outsideDiff  = new ArrayList<>();

        for(AiReviewResponse.Finding f: ai.findings()){
            if (isInDiff(f, linesByFile)) {
                inline.add(new GitHubCreateReviewRequest.Comment(
                        f.file(),f.line(),"RIGHT",formatComment(f)
                ));

            }else{
                outsideDiff.add(f);
            }

        }

        // 3. Build the request
        GitHubCreateReviewRequest request = new GitHubCreateReviewRequest(
                pr.headSha(),
                "COMMENT",
                formatBody(ai.summary(), outsideDiff),
                inline);

        // 4. Send it
        GitHubReviewResponse  response = gitHubClient.createReview(owner,repo,pr.number(),request);
        log.info("Posted review with {} inline comments: {}",inline.size(),response.htmlUrl());
        return response;
    }

    private String formatComment(AiReviewResponse.Finding finding){
        String text = "**" + finding.severity() + "**" + finding.message();
        if(finding.suggestion() != null && !finding.suggestion().isBlank()){
            text = text + "\n\n**Suggestion**" + finding.suggestion();
        }
        return text;
    }

    private String formatBody(String summary , List<AiReviewResponse.Finding> outsideDiff) {
        StringBuilder body = new StringBuilder();
        body.append("##AI review \n\n");
        body.append(summary);

        if(!outsideDiff.isEmpty()) {
            body.append("\n\n### Findings outside the diff\n");
            for(AiReviewResponse.Finding f : outsideDiff){
                body.append("\n- **").append(f.severity()).append("** `").append(f.file()).append("`");
                if(f.line() != null){
                    body.append(" line ").append(f.line());}
                body.append(": ").append(f.message());
            }
        }return body.toString();
    }

}
