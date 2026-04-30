package com.nick.job_application_tracker.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.service.implementation.WorkspaceExportService;
import com.nick.job_application_tracker.service.implementation.WorkspaceExportService.ExportPayload;

@WebMvcTest(
    controllers = ExportController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkspaceExportService workspaceExportService;

    @Test
    void exportWorkspaceReturnsAttachment() throws Exception {
        byte[] payload = "{\"applications\":[]}".getBytes(StandardCharsets.UTF_8);
        when(workspaceExportService.exportWorkspace("json"))
            .thenReturn(new ExportPayload(payload, "workspace.json", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/exports/workspace").param("format", "json"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"workspace.json\""))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("{\"applications\":[]}"));
    }
}
