package com.nick.job_application_tracker.integration;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrontendShellIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootServesFrontendShell() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("index.html"));

        mockMvc.perform(get("/index.html"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("JobTrackr")))
            .andExpect(content().string(containsString("id=\"root\"")))
            .andExpect(content().string(containsString("/assets/")))
            .andExpect(content().string(containsString("type=\"module\"")))
            .andExpect(content().string(containsString("rel=\"stylesheet\"")));
    }

    @Test
    void staticAssetsArePubliclyAccessible() throws Exception {
        String html = mockMvc.perform(get("/index.html"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String jsPath = extractAssetPath(html, "<script[^>]+src=\"([^\"]+\\.js)\"");
        String cssPath = extractAssetPath(html, "<link[^>]+href=\"([^\"]+\\.css)\"");

        assertNotNull(jsPath, "Expected index.html to reference a JavaScript asset");
        assertNotNull(cssPath, "Expected index.html to reference a stylesheet asset");

        mockMvc.perform(get(jsPath))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("JobTrackr")));

        mockMvc.perform(get(cssPath))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(".workspace-shell")));
    }

    private String extractAssetPath(String html, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }
}
