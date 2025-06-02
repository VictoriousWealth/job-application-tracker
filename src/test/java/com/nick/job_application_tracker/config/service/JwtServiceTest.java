package com.nick.job_application_tracker.config.service;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateAndValidateToken() throws Exception {
        String username = "test@example.com";
        String role = "basic";

        String token = jwtService.generateToken(username, role);
        System.out.println("Generated token: " + token);
        assertNotNull(token, "Generated token should not be null");

        String[] result = jwtService.validateTokenAndGetUsernameAndRole(token);
        System.out.println("Decoded token: " + result[0] + ", " + result[1]);
        assertEquals(username, result[0], "Extracted username should match");
        assertEquals(role.toUpperCase(), result[1], "Extracted role should match (in uppercase)");
    }

    @Test
    void testExtractEmailAndRole() throws Exception {
        String email = "hello@world.com";
        String role = "manager";
        String token = jwtService.generateToken(email, role);

        assertEquals(email, jwtService.extractEmail(token));
        assertEquals(role.toUpperCase(), jwtService.extractRole(token));
    }

    @Test
    void testTamperedTokenFailsValidation() throws Exception {
        String token = jwtService.generateToken("bad@actor.com", "user");

        // Tamper with payload
        String[] parts = token.split("\\.");
        String tamperedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"hacker\",\"role\":\"admin\",\"iss\":\"FakeIssuer\",\"iat\":0,\"exp\":9999999999}".getBytes());
        String tamperedToken = parts[0] + "." + tamperedPayload + "." + parts[2];

        String[] result = jwtService.validateTokenAndGetUsernameAndRole(tamperedToken);
        assertNull(result[0]);
        assertNull(result[1]);
    }
}
