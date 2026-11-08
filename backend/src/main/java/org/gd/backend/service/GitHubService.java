package org.gd.backend.service;

import org.gd.backend.client.GitHubClient;
import org.gd.backend.dto.github.GitHubRepoResponse;
import org.gd.backend.dto.RepoInfoResponse;
import org.springframework.stereotype.Service;

@Service
public class GitHubService {
    public final GitHubClient gitHubClient;
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }
    public RepoInfoResponse getRepoInfo(String owner, String repo) {
        GitHubRepoResponse gh = gitHubClient.getRepository(owner, repo);

        //  return a new RepoInfoResponse using the fields from gh
        RepoInfoResponse res = new RepoInfoResponse(gh.fullName(), gh.defaultBranch(), gh.isPrivate(),gh.htmlUrl());

        return res;

    }
}
