package org.gd.backend.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record  GitHubRepoResponse(
    @JsonProperty("full_name") String fullName,
    @JsonProperty("default_branch") String defaultBranch,
    @JsonProperty("html_url") String htmlUrl,
    @JsonProperty("private") boolean  isPrivate
){}
