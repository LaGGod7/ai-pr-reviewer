package org.gd.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void health_returnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))                        // build a fake GET request
                .andExpect(status().isOk())                           // HTTP 200
                .andExpect(jsonPath("$.status").value("UP"))          // body.status == "UP"
                .andExpect(jsonPath("$.service").value("pr-reviewer-backend"));
    }
}