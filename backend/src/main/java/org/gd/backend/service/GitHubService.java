package org.gd.backend.service;

import org.gd.backend.client.GitHubClient;
import org.gd.backend.dto.FileStatus;
import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.github.GitHubFileResponse;
import org.gd.backend.dto.github.GitHubPullRequestResponse;
import org.gd.backend.dto.github.GitHubRepoResponse;
import org.gd.backend.dto.RepoInfoResponse;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class GitHubService {
    public final GitHubClient gitHubClient;
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }
    public RepoInfoResponse getRepoInfo(String owner, String repo) {
        GitHubRepoResponse gh = gitHubClient.getRepository(owner, repo);

        //  return a new RepoInfoResponse using the fields from gh

        return new RepoInfoResponse(gh.fullName(), gh.defaultBranch(), gh.isPrivate(),gh.htmlUrl());

    }

    public PullRequestDetailsResponse getPullRequestDetails(String owner, String repo,int number) {
        GitHubPullRequestResponse pr = gitHubClient.getPullRequest(owner, repo, number);
        List<GitHubFileResponse> files = gitHubClient.getPullRequestFiles(owner, repo, number);

        List<PullRequestDetailsResponse.ChangedFile> changedFiles = files.stream()
                .map(this::toChangedFile)
                .toList();

        return new PullRequestDetailsResponse(
                pr.number(),
                pr.title(),
                pr.user().login(),
                pr.head().ref(),
                pr.base().ref(),
                pr.head().sha(),
                pr.htmlUrl(),
                changedFiles
        );
    }
    private PullRequestDetailsResponse.ChangedFile toChangedFile(GitHubFileResponse file) {

        return new PullRequestDetailsResponse.ChangedFile(
                file.filename(),
                FileStatus.fromGitHub(file.status()),
                file.additions(),
                file.deletions(),
                file.patch()
        );

    }
}
