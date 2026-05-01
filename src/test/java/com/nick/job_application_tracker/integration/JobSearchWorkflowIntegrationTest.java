package com.nick.job_application_tracker.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
class JobSearchWorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticatedUserCanCompleteCoreWorkflowAndReadExtendedViews() throws Exception {
        String email = "workflow@example.com";
        String password = "Password123!";

        signup(email, password);
        String token = login(email, password);

        mockMvc.perform(get("/api/auth/me").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.roles[0]").value("BASIC"));

        UUID sourceId = createResource(
            "/api/job-sources",
            token,
            Map.of("name", "LinkedIn"),
            201
        );
        UUID locationId = createResource(
            "/api/locations",
            token,
            Map.of("city", "London", "country", "United Kingdom"),
            201
        );
        UUID resumeId = createResource(
            "/api/resumes",
            token,
            Map.of("title", "Backend Resume", "filePath", "/docs/backend-resume.pdf"),
            201
        );
        UUID coverLetterId = createResource(
            "/api/cover-letters",
            token,
            Map.of(
                "title", "Platform Cover Letter",
                "filePath", "/docs/platform-cover-letter.pdf",
                "content", "Java Spring distributed systems"
            ),
            201
        );

        UUID applicationId = createResource(
            "/api/job-applications",
            token,
            Map.ofEntries(
                Map.entry("jobTitle", "Backend Engineer"),
                Map.entry("company", "OpenAI"),
                Map.entry("status", "APPLIED"),
                Map.entry("sourceId", sourceId.toString()),
                Map.entry("locationId", locationId.toString()),
                Map.entry("resumeId", resumeId.toString()),
                Map.entry("coverLetterId", coverLetterId.toString()),
                Map.entry("jobDescription", "Java Spring Docker APIs and distributed systems"),
                Map.entry("notes", "Focus on platform work"),
                Map.entry("appliedOn", "2026-05-01T09:00:00"),
                Map.entry("deadline", "2026-05-10T17:00:00")
            ),
            201
        );

        createResource(
            "/api/attachments",
            token,
            Map.of(
                "type", "JOB_DESCRIPTION",
                "filePath", "/docs/openai-job-description.pdf",
                "jobApplicationId", applicationId.toString()
            ),
            201
        );

        createResource(
            "/api/communications",
            token,
            Map.of(
                "type", "EMAIL",
                "direction", "OUTBOUND",
                "timestamp", "2026-05-01T10:00:00",
                "message", "Reached out to the recruiter",
                "jobApplicationId", applicationId.toString()
            ),
            201
        );

        createResource(
            "/api/skills",
            token,
            Map.of(
                "skillName", "Java",
                "jobApplicationId", applicationId.toString()
            ),
            200
        );

        LocalDateTime tomorrowMorning = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);

        createResource(
            "/api/follow-ups",
            token,
            Map.of(
                "remindOn", tomorrowMorning.plusHours(2).toString(),
                "note", "Check in with the recruiter",
                "jobApplicationId", applicationId.toString()
            ),
            201
        );

        createResource(
            "/api/scheduled-communications",
            token,
            Map.of(
                "type", "INTERVIEW",
                "scheduledFor", tomorrowMorning.toString(),
                "notes", "System design interview",
                "jobApplicationId", applicationId.toString()
            ),
            201
        );

        mockMvc.perform(get("/api/job-applications/{id}", applicationId).header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.company").value("OpenAI"))
            .andExpect(jsonPath("$.sourceId").value(sourceId.toString()));

        mockMvc.perform(get("/api/attachments/job/{jobAppId}", applicationId).header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("JOB_DESCRIPTION"));

        mockMvc.perform(get("/api/communications/job/{jobAppId}", applicationId).header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("EMAIL"));

        mockMvc.perform(get("/api/follow-ups/job/{jobAppId}", applicationId).header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].note").value("Check in with the recruiter"));

        mockMvc.perform(get("/api/scheduled-communications").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("INTERVIEW"));

        mockMvc.perform(get("/api/timelines/job/{jobAppId}", applicationId).header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].eventType").value("APPLICATION_CREATED"));

        mockMvc.perform(get("/api/insights/dashboard").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalApplications").value(1))
            .andExpect(jsonPath("$.statusBreakdown.APPLIED").value(1))
            .andExpect(jsonPath("$.upcomingEvents[0].company").value("OpenAI"));

        mockMvc.perform(get("/api/insights/analytics").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalApplications").value(1))
            .andExpect(jsonPath("$.upcomingScheduledEvents").value(1));

        mockMvc.perform(get("/api/insights/recommendations").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("PREPARE_EVENT")));

        mockMvc.perform(get("/api/matching/applications/{id}", applicationId).header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.company").value("OpenAI"))
            .andExpect(jsonPath("$.overallScore", greaterThan(0.0)))
            .andExpect(jsonPath("$.matchedSkills", hasItem("Java")));

        mockMvc.perform(get("/api/calendar/events").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("SCHEDULED_COMMUNICATION")))
            .andExpect(content().string(containsString("FOLLOW_UP_REMINDER")));

        mockMvc.perform(get("/api/calendar/events.ics").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/calendar"))
            .andExpect(content().string(containsString("BEGIN:VCALENDAR")))
            .andExpect(content().string(containsString("Interview: OpenAI - Backend Engineer")));

        mockMvc.perform(get("/api/exports/workspace").param("format", "json").header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(containsString("\"company\":\"OpenAI\"")));
    }

    @Test
    void usersCannotAccessAnotherUsersApplicationWorkspace() throws Exception {
        String ownerToken = loginAfterSignup("owner@example.com", "Password123!");
        String outsiderToken = loginAfterSignup("outsider@example.com", "Password123!");

        UUID sourceId = createResource(
            "/api/job-sources",
            ownerToken,
            Map.of("name", "Referral"),
            201
        );

        UUID applicationId = createResource(
            "/api/job-applications",
            ownerToken,
            Map.of(
                "jobTitle", "Platform Engineer",
                "company", "Anthropic",
                "status", "APPLIED",
                "sourceId", sourceId.toString(),
                "jobDescription", "Java platform APIs",
                "notes", "Owner application"
            ),
            201
        );

        mockMvc.perform(get("/api/job-applications/{id}", applicationId).header(AUTHORIZATION, bearer(outsiderToken)))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/matching/applications/{id}", applicationId).header(AUTHORIZATION, bearer(outsiderToken)))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/follow-ups")
                .header(AUTHORIZATION, bearer(outsiderToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "remindOn", LocalDateTime.now().plusDays(1).withNano(0).toString(),
                    "note", "Unauthorized reminder",
                    "jobApplicationId", applicationId.toString()
                ))))
            .andExpect(status().isNotFound());
    }

    @Test
    void jobApplicationsEndpointSupportsPaginationAndSorting() throws Exception {
        String token = loginAfterSignup("pagination@example.com", "Password123!");
        UUID sourceId = createResource(
            "/api/job-sources",
            token,
            Map.of("name", "Company Site"),
            201
        );

        createJobApplication(token, sourceId, "Platform Engineer", "Beta");
        createJobApplication(token, sourceId, "Infrastructure Engineer", "Alpha");
        createJobApplication(token, sourceId, "Distributed Systems Engineer", "Gamma");

        mockMvc.perform(get("/api/job-applications")
                .param("page", "0")
                .param("size", "2")
                .param("sort", "company,asc")
                .header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.content[0].company").value("Alpha"))
            .andExpect(jsonPath("$.content[1].company").value("Beta"));

        mockMvc.perform(get("/api/job-applications")
                .param("page", "1")
                .param("size", "2")
                .param("sort", "company,asc")
                .header(AUTHORIZATION, bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.number").value(1))
            .andExpect(jsonPath("$.content[0].company").value("Gamma"));
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

    private UUID createResource(String path, String token, Object payload, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(post(path)
                .header(AUTHORIZATION, bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().is(expectedStatus))
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

        return UUID.fromString(readJson(result).get("id").asText());
    }

    private UUID createJobApplication(String token, UUID sourceId, String jobTitle, String company) throws Exception {
        return createResource(
            "/api/job-applications",
            token,
            Map.of(
                "jobTitle", jobTitle,
                "company", company,
                "status", "APPLIED",
                "sourceId", sourceId.toString(),
                "jobDescription", "Java APIs and distributed systems",
                "notes", "Pagination coverage"
            ),
            201
        );
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
