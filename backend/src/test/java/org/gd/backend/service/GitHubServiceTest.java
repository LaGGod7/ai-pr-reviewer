package org.gd.backend.service;

import org.gd.backend.client.GitHubClient;
import org.gd.backend.dto.FileStatus;
import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.github.GitHubFileResponse;
import org.gd.backend.dto.github.GitHubPullRequestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {
    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    private GitHubPullRequestResponse samplePr() {
        return new GitHubPullRequestResponse(7, "Add login", "open", "https://github.com/octo/hello/pull/7",
                new GitHubPullRequestResponse.User("octocat"),
                new GitHubPullRequestResponse.BranchRef("feature/login", "abc123"),
                new GitHubPullRequestResponse.BranchRef("main", "def456"));
    }

    @Test
    void getPullRequestDetails_mapsGitHubDataToOurDto() {
        //Arrange

        var file = new GitHubFileResponse("src/Login.java", "modified", 10, 2, "@@ -1,2 +1,10 @@");
        when(gitHubClient.getPullRequest("octo", "hello", 7)).thenReturn(samplePr());
        when(gitHubClient.getPullRequestFiles("octo", "hello", 7)).thenReturn(List.of(file));

        //Act
        PullRequestDetailsResponse result = gitHubService.getPullRequestDetails("octo", "hello", 7);

        //Assert
        assertEquals("octocat", result.author());
        assertEquals("feature/login", result.sourceBranch());
        assertEquals("main", result.targetBranch());
        assertEquals("abc123", result.headSha());
        assertEquals(1, result.files().size());
        assertEquals(FileStatus.MODIFIED, result.files().get(0).status());

    }

    @Test
    void getPullRequestDetails_unknownStatus_becomesOther() {
        // Arrange
        var file = new GitHubFileResponse("src/Login.java", "copied", 10, 2, "@@ -1,2 +1,10 @@");
        when(gitHubClient.getPullRequest("octo", "hello", 7)).thenReturn(samplePr());
        when(gitHubClient.getPullRequestFiles("octo", "hello", 7)).thenReturn(List.of(file));

        // Act
        PullRequestDetailsResponse result = gitHubService.getPullRequestDetails("octo", "hello", 7);


        //Assert
        assertEquals(FileStatus.OTHER, result.files().get(0).status());
    }

}