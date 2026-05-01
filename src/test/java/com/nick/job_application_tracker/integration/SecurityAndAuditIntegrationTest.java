package com.nick.job_application_tracker.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.interfaces.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SecurityAndAuditIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void auditEndpointsRequireAdminRoleAndAdminCanReadAuditHistory() throws Exception {
        String adminEmail = "admin@example.com";
        String password = "Password123!";
        signup(adminEmail, password);
        promoteToAdmin(adminEmail);

        String adminToken = login(adminEmail, password);
        String basicToken = loginAfterSignup("basic@example.com", password);

        mockMvc.perform(get("/api/audit-log").header(AUTHORIZATION, bearer(basicToken)))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/audit-log")
                .header(AUTHORIZATION, bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "action", "CREATE",
                    "description", "Manual audit check"
                ))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.action").value("CREATE"))
            .andExpect(jsonPath("$.description").value("Manual audit check"))
            .andExpect(jsonPath("$.userId").exists());

        mockMvc.perform(get("/api/audit-log").header(AUTHORIZATION, bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].description", hasItem("Manual audit check")));
    }

    @Test
    void protectedEndpointsAndWorkspaceFailuresReturnExpectedResponses() throws Exception {
        mockMvc.perform(get("/api/job-applications"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Authorization header is missing"));

        String token = loginAfterSignup("failure@example.com", "Password123!");

        mockMvc.perform(get("/api/exports/workspace")
                .param("format", "xml")
                .header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Unsupported export format")));

        mockMvc.perform(post("/api/job-applications")
                .header(AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "jobTitle", "Backend Engineer",
                    "company", "OpenAI",
                    "status", "APPLIED",
                    "sourceId", UUID.randomUUID().toString(),
                    "jobDescription", "Java APIs",
                    "notes", "Missing source failure"
                ))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("The requested resource was not found."));
    }

    @Test
    void disabledUsersCannotLogInOrReuseExistingTokens() throws Exception {
        String email = "disabled@example.com";
        String password = "Password123!";

        signup(email, password);
        String token = login(email, password);
        disableUser(email);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
            .andExpect(status().isForbidden())
            .andExpect(content().string(containsString("disabled")));

        mockMvc.perform(get("/api/auth/refresh-token").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    private String loginAfterSignup(String email, String password) throws Exception {
        signup(email, password);
        return login(email, password);
    }

    private void signup(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Signup successful")));
    }

    private String login(String email, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andReturn();

        return readJson(loginResult).get("token").asText();
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private void promoteToAdmin(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email).orElseThrow();
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    private void disableUser(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email).orElseThrow();
        user.setEnabled(false);
        userRepository.save(user);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
