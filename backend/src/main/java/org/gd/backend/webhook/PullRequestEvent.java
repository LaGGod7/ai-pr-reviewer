package org.gd.backend.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PullRequestEvent(
        String action,
        int number,
        @JsonProperty("pull_request") PullRequest pullRequest,
        Repository repository)
{
    public record PullRequest(Head head){}
    public record Head(String sha){}
    public record Repository(String name,Owner owner){}
    public record Owner(String login){}
}
