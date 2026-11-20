package org.gd.backend.dto.github;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

public record GitHubCreateReviewRequest(
        @JsonProperty("commit_id") String commitId,
        String event,
        String body,
        List<Comment> comments){
       public record Comment(String path,int line,String side,String body)    {}
}

