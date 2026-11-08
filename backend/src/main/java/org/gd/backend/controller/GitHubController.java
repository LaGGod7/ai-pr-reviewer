package org.gd.backend.controller;

import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.RepoInfoResponse;
import org.gd.backend.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/github")
public class GitHubController {
    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService)
    {
        this.gitHubService = gitHubService;
    }

    @GetMapping("repos/{owner}/{repo}")
    public RepoInfoResponse getRepoInfo(@PathVariable String owner, @PathVariable String repo)
    {
      return gitHubService.getRepoInfo(owner, repo);
    };

    @GetMapping("/repos/{owner}/{repo}/pulls/{number}")
    public PullRequestDetailsResponse getPullReqInfo(@PathVariable String owner, @PathVariable String repo, @PathVariable int number)
    {
        return gitHubService.getPullRequestDetails(owner, repo, number);
    }
}
