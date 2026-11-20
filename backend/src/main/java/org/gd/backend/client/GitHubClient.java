package org.gd.backend.client;

import org.gd.backend.dto.github.*;

import org.gd.backend.exception.GitHubResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Component
public class GitHubClient {

    private final RestClient restClient;
    public GitHubClient(@Qualifier("gitHubRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public GitHubRepoResponse getRepository(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}",owner,repo)
                .retrieve()
                .body(GitHubRepoResponse.class);
    }
    public GitHubPullRequestResponse getPullRequest(String owner, String repo, int number) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/pulls/{number}", owner, repo, number)
                .retrieve()
                .body(GitHubPullRequestResponse.class);
    }

    public List<GitHubFileResponse> getPullRequestFiles(String owner, String repo, int number) {

        return restClient.get()
                .uri("/repos/{owner}/{repo}/pulls/{number}/files?per_page=100",owner,repo,number)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GitHubFileResponse>>() {
                });

    }
    public GitHubReviewResponse createReview(String owner, String repo, int number , GitHubCreateReviewRequest request) {
        return restClient.post()
                .uri("/repos/{owner}/{repo}/pulls/{number}/reviews",owner,repo,number)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(GitHubReviewResponse.class);
    }
}
