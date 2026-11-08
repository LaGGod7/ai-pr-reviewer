package org.gd.backend.controller;

import org.gd.backend.dto.RepoInfoResponse;
import org.gd.backend.exception.RepositoryNotFoundException;
import org.gd.backend.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(GitHubController.class)
class GitHubControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubService gitHubService;

    @Test
    void getRepo_returnsRepoInfoResponse() throws Exception {
        // Arrange: tell the mock what to return
        RepoInfoResponse fake = new RepoInfoResponse(
                "octo/hello", "main", false, "https://github.com/octo/hello");
        when(gitHubService.getRepoInfo("octo", "hello")).thenReturn(fake);

        // Act + Assert: call our endpoint and check the response
        mockMvc.perform(get("/api/v1/github/repos/octo/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("octo/hello"))
                .andExpect(jsonPath("$.defaultBranch").value("main"));
    }
    @Test
    void getRepo_whenNotFound_returns404()  throws Exception {
        //Arrange
        when(gitHubService.getRepoInfo("octo", "missing")).thenThrow(new RepositoryNotFoundException("octo", "missing"));
        mockMvc.perform(get("/api/v1/github/repos/octo/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Repository not found"))
                .andExpect(jsonPath("$.detail").value("Repository octo/missing not found"));

    }

}