package org.gd.backend.client;

import org.gd.backend.dto.github.GitHubFileResponse;
import org.gd.backend.dto.github.GitHubPullRequestResponse;
import org.gd.backend.dto.github.GitHubRepoResponse;

import org.gd.backend.exception.GitHubResourceNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Component
public class GitHubClient {
    private final RestClient restClient;
    public GitHubClient(RestClient restClient) {
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
}
