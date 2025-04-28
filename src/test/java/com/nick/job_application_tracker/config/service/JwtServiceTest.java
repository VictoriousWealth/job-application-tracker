package com.nick.job_application_tracker.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testService() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/JwtService"))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
