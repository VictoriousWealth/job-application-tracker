package com.nick.job_application_tracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
class FollowUpReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testController() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/FollowUpReminderController"))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
