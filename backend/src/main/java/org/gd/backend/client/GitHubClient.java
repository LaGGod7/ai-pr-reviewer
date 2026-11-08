package org.gd.backend.client;

import org.gd.backend.dto.github.GitHubRepoResponse;

import org.gd.backend.exception.RepositoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;




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
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (request, response) -> {
            throw new RepositoryNotFoundException(owner, repo);
        })
                .body(GitHubRepoResponse.class);
    }
}
