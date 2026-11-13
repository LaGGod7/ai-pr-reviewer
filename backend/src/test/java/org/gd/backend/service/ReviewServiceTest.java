package org.gd.backend.service;

import org.gd.backend.Entity.ReviewEntity;
import org.gd.backend.repository.ReviewRepository;
import org.gd.backend.client.AiReviewClient;
import org.gd.backend.dto.FileStatus;
import org.gd.backend.dto.PullRequestDetailsResponse;
import org.gd.backend.dto.PullRequestReviewResponse;
import org.gd.backend.dto.ai.AiReviewRequest;
import org.gd.backend.dto.ai.AiReviewResponse;
import org.gd.backend.exception.AiServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private GitHubService gitHubService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AiReviewClient aiReviewClient;

    @InjectMocks
    private ReviewService reviewService;

    private PullRequestDetailsResponse samplePr()
    {      return new PullRequestDetailsResponse(
            5, "Add login", "octocat", "feature/login", "main", "abc123",
            "https://github.com/octo/hello/pull/5",
            List.of(
                    new PullRequestDetailsResponse.ChangedFile(
                            "Auth.java", FileStatus.MODIFIED, 3, 0, "@@ -0,0 +1,3 @@\n+code"),
                    new PullRequestDetailsResponse.ChangedFile(
                            "Old.java", FileStatus.REMOVED, 0, 2, "@@ -1,2 +0,0 @@\n-old")
            ));
    }

    @Test
    void reviewPullRequest_combinesPrInfoAndAiFindings()
    {   //Arrange
        when(gitHubService.getPullRequestDetails("octo","hello",5)).thenReturn(samplePr());
        var aiResponse = new AiReviewResponse(
                "One problem found",
                List.of(new AiReviewResponse.Finding(
                        "Auth.java", 2, "CRITICAL", "Hardcoded password", "Use a secret store")),
                1, false);
        when(aiReviewClient.review(any(AiReviewRequest.class))).thenReturn(aiResponse);

        //Act
        PullRequestReviewResponse result = reviewService.reviewPullRequest("octo","hello",5);

        // Assert
        assertEquals(5, result.number());                            // from GitHub
        assertEquals("Add login", result.title());                   // from GitHub
        assertEquals("One problem found", result.summary());         // from the AI
        assertEquals(1, result.findings().size());                   // from the AI
        assertEquals("CRITICAL", result.findings().get(0).severity());

    }
    @Test
    void reviewPullRequest_sendsTitleStatusesToAi(){
        //Arrange
        when(gitHubService.getPullRequestDetails("octo","hello",5)).thenReturn(samplePr());
        when(aiReviewClient.review(any(AiReviewRequest.class))).thenReturn(new AiReviewResponse("ok", List.of(), 1, false));
        ArgumentCaptor<AiReviewRequest> captor = ArgumentCaptor.forClass(AiReviewRequest.class);

        //Act
        reviewService.reviewPullRequest("octo","hello",5);

        //Assert: inspect what was actually sent to Python
        verify(aiReviewClient).review(captor.capture());
        AiReviewRequest sent = captor.getValue();
        assertEquals("Add login", sent.title());
        assertEquals("MODIFIED", sent.files().get(0).status());
        assertEquals("REMOVED", sent.files().get(1).status());

    }

    @Test
    void reviewPullRequest_whenAiUnavailable_throws() {
        // Arrange
        when(gitHubService.getPullRequestDetails("octo", "hello", 5)).thenReturn(samplePr());
        when(aiReviewClient.review(any(AiReviewRequest.class)))
                .thenThrow(new AiServiceUnavailableException("down"));

        // Act + Assert
        assertThrows(AiServiceUnavailableException.class,
                () -> reviewService.reviewPullRequest("octo", "hello", 5));
        verify(reviewRepository, never()).save(any());
    }
    @Test
    void reviewPullRequest_savesReviewWithFindings() {
        // Arrange: same stubbing as reviewPullRequest_combinesPrInfoAndAiFindings
        when(gitHubService.getPullRequestDetails("octo","hello",5)).thenReturn(samplePr());
        var aiResponse = new AiReviewResponse(
                "One problem found",
                List.of(new AiReviewResponse.Finding(
                        "Auth.java", 2, "CRITICAL", "Hardcoded password", "Use a secret store")),
                1, false);
        when(aiReviewClient.review(any(AiReviewRequest.class))).thenReturn(aiResponse);

        reviewService.reviewPullRequest("octo", "hello", 5);

        ArgumentCaptor<ReviewEntity> captor = ArgumentCaptor.forClass(ReviewEntity.class);
        verify(reviewRepository).save(captor.capture());

        ReviewEntity saved = captor.getValue();
        assertEquals("octo/hello", saved.getRepoFullName());
        assertEquals(5, saved.getPrNumber());
        assertEquals(1, saved.getFindings().size()); // match the number of findings your fake AI response has
    }

}