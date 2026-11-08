package org.gd.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubPullRequestResponse(
        int number,
        String title,
        String state,
        @JsonProperty("html_url")String htmlUrl,
        User user,
        BranchRef head,
        BranchRef base)
{

    public record User(String login) { }
    public record BranchRef(String ref,String sha) {}
}
