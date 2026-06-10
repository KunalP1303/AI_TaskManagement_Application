package com.kunal.taskmanager.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskManagerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;
    private static Long createdTaskId;

    @Test
    @Order(1)
    void register_withValidCredentials_returns200() throws Exception{
        Map<String, String> request = Map.of(
                "username", "integrationuser",
                "password","password123"

        );

        mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void register_withDuplicateUsername_returns400() throws Exception{
        Map<String, String> request = Map.of(
                "username", "integrationuser",
                "password","password123"

        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(3)
    void login_withValidCredentials_returnsJwt() throws Exception{
        Map<String, String> request = Map.of(
                "username", "integrationuser",
                "password","password123"

        );

        String response = mockMvc.perform(post("/api/v1/auth/login")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isNotEmpty())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

        jwtToken = objectMapper.readTree(response)
                                .get("data")
                                .asText();

        assertThat(jwtToken).isNotBlank();
    }

    @Test
    @Order(4)
    void login_withWrongPassword_returns400() throws Exception{
        Map<String, String> request = Map.of(
                "username", "integrationuser",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(5)
    void getTasks_withoutToken_returns403() throws Exception{
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    void getTasks_withValidToken_returnsEmptyList() throws Exception{
        mockMvc.perform(get("/api/v1/tasks")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(7)
    void createTask_withValidToken_returns201() throws Exception{
        Map<String, String> request = Map.of(
                "title", "Integration Test Task",
                "description", "Created during integration test",
                "priority", "HIGH",
                "status", "TODO"
        );

        String response = mockMvc.perform(post("/api/v1/tasks")
                                            .header("Authorization", "Bearer " + jwtToken)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.title").value("Integration Test Task"))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

        createdTaskId = objectMapper.readTree(response)
                .get("data")
                .get("id")
                .asLong();

        assertThat(createdTaskId).isPositive();
    }

    @Test
    @Order(8)
    void updateTask_withValidToken_returns200() throws Exception{
        Map<String, String> request = Map.of(
                "title", "Updated Task Title",
                "description", "Updated during integration test",
                "priority", "LOW",
                "status", "IN_PROGRESS"
        );

        mockMvc.perform(put("/api/v1/tasks/" + createdTaskId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated Task Title"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @Order(9)
    void deleteTask_withValidToken_returns200() throws Exception{
        mockMvc.perform(delete("/api/v1/tasks/" + createdTaskId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(10)
    void getTask_afterDeletion_returns404() throws Exception{
        mockMvc.perform(get("/api/v1/tasks/" + createdTaskId)
                        .header("Authorization", "Bearer " +jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
