package org.gd.backend.client;

import org.gd.backend.dto.github.GitHubRepoResponse;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;




@Component
public class GithubClient {
    private final RestClient restClient;
    public GithubClient(RestClient restClient) {
        this.restClient = restClient;
    }
    public GitHubRepoResponse getRepository(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}",owner,repo)
                .retrieve()
                .body(GitHubRepoResponse.class);
    }
}
