package com.nick.job_application_tracker.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WorkspaceFrontendRegressionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void changingEmailKeepsDocumentLibraryAccessibleAfterRelogin() throws Exception {
        String originalEmail = "library-owner@example.com";
        String updatedEmail = "library-owner-renamed@example.com";
        String password = "Password123!";

        String token = loginAfterSignup(originalEmail, password);

        createResource(
            "/api/resumes",
            token,
            Map.of("title", "Backend Resume", "filePath", "/docs/backend-resume.pdf"),
            201
        );
        createResource(
            "/api/cover-letters",
            token,
            Map.of(
                "title", "Platform Cover Letter",
                "filePath", "/docs/platform-cover-letter.pdf",
                "content", "Tailored content"
            ),
            201
        );

        mockMvc.perform(patch("/api/users/me")
                .header(AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", updatedEmail))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(updatedEmail));

        mockMvc.perform(get("/api/auth/me").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));

        String refreshedToken = login(updatedEmail, password);

        mockMvc.perform(get("/api/resumes").header(AUTHORIZATION, bearer(refreshedToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Backend Resume"));

        mockMvc.perform(get("/api/cover-letters").header(AUTHORIZATION, bearer(refreshedToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Platform Cover Letter"));
    }

    @Test
    void sharedMetadataCreatesAreIdempotentForSourcesAndLocations() throws Exception {
        String password = "Password123!";
        String firstToken = loginAfterSignup("metadata-one@example.com", password);
        String secondToken = loginAfterSignup("metadata-two@example.com", password);

        UUID firstSourceId = createResource(
            "/api/job-sources",
            firstToken,
            Map.of("name", "LinkedIn"),
            201
        );
        UUID firstLocationId = createResource(
            "/api/locations",
            firstToken,
            Map.of("city", "London", "country", "United Kingdom"),
            201
        );

        mockMvc.perform(post("/api/job-sources")
                .header(AUTHORIZATION, bearer(secondToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "LinkedIn"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(firstSourceId.toString()));

        mockMvc.perform(post("/api/locations")
                .header(AUTHORIZATION, bearer(secondToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("city", "London", "country", "United Kingdom"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(firstLocationId.toString()))
            .andExpect(jsonPath("$.city").value("London"))
            .andExpect(jsonPath("$.country").value("United Kingdom"));
    }

    @Test
    void updatingEmailToAnExistingAddressReturnsConflict() throws Exception {
        String password = "Password123!";
        String firstToken = loginAfterSignup("conflict-a@example.com", password);
        loginAfterSignup("conflict-b@example.com", password);

        mockMvc.perform(patch("/api/users/me")
                .header(AUTHORIZATION, bearer(firstToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", "conflict-b@example.com"))))
            .andExpect(status().isConflict())
            .andExpect(content().string(containsString("Email already in use.")));
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

    private UUID createResource(String path, String token, Map<String, Object> payload, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .header(AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().is(expectedStatus))
            .andReturn();

        return UUID.fromString(readJson(result).get("id").asText());
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
