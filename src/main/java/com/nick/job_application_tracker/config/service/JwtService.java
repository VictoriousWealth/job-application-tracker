package com.nick.job_application_tracker.config.service;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MIN_SECRET_LENGTH_BYTES = 32;

    private final byte[] secretBytes;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
        @Value("${app.security.jwt.secret:}") String configuredSecret,
        @Value("${app.security.jwt.issuer:JobTrackerApplicationBackend}") String issuer,
        @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.secretBytes = resolveSecretBytes(configuredSecret);
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String[] validateTokenAndGetUsernameAndRole(String token) throws Exception {
        if (token == null || token.isBlank()) {
            return new String[2];
        }
        String[] split = token.split("\\.");
        if (split.length != 3) {
            return new String[2];
        }
        String decodedPayload = decodeToString(split[1]);

        if (isTokenTampered(split[0]+"."+split[1], split[2])) return new String[2];

        if (isIssuerNotTrusted(decodedPayload)) return new String[2];
        if (isTokenExpired(decodedPayload)) return new String[2];


        return extractUsernameAndRole(decodedPayload);
    }

    private String[] extractUsernameAndRole(String decodedPayload) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(decodedPayload);

        String username = payloadJson.get("sub").asText();
        String role = payloadJson.get("role").asText().toUpperCase();

        return new String[]{username, role};
    }

    private boolean isIssuerNotTrusted(String decodedPayload) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(decodedPayload);

        String issuer = payloadJson.get("iss").asText();
        return !this.issuer.equals(issuer);
    }

    private boolean isTokenExpired(String decodedPayload) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(decodedPayload);

        long issueTime = payloadJson.get("iat").asLong();
        long expiryTime = payloadJson.get("exp").asLong();

        long timeInSeconds = System.currentTimeMillis()/1000;
        return !(issueTime <= timeInSeconds && timeInSeconds < expiryTime);
    }

    private boolean isTokenTampered(String message, String signatureToMatchWith) throws NoSuchAlgorithmException, InvalidKeyException {
        String signature = generateSignature(message);
        String normalizedSignature = signatureToMatchWith.replaceAll("=+$", "");
        return !MessageDigest.isEqual(signature.getBytes(), normalizedSignature.getBytes());
    }

    //TODO instance could be based on header values
    private String generateSignature(String message) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(message.getBytes());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private String decodeToString(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    public String generateToken(String username, String role) throws NoSuchAlgorithmException, InvalidKeyException {
        String header = "{\n" +
                "  \"alg\": \"HS256\",\n" +
                "  \"typ\": \"JWT\"\n" +
                "}";
        long timeInSeconds = System.currentTimeMillis() / 1000;
        String payload = "{\n" +
                "  \"iss\": \"" + issuer + "\",\n" +
                "  \"sub\": \""+username+"\",\n" +
                "  \"role\": \""+role+"\",\n" +
                "  \"iat\": "+ timeInSeconds +",\n" +
                "  \"exp\": "+(timeInSeconds + expirationSeconds)+"\n" +
                "}";

        header = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());

        return header+"."+payload+"."+generateSignature(header+"."+payload);
    }

    public String extractEmail(String token) throws Exception {
        String[] split = token.split("\\.");
        String payload = decodeToString(split[1]);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(payload);
        return payloadJson.get("sub").asText();
    }
    
    public String extractRole(String token) throws Exception {
        String[] split = token.split("\\.");
        String payload = decodeToString(split[1]);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(payload);
        return payloadJson.get("role").asText().toUpperCase();
    }

    private byte[] resolveSecretBytes(String configuredSecret) {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            byte[] generatedSecret = new byte[MIN_SECRET_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(generatedSecret);
            log.warn("JWT secret is not configured; generated an ephemeral in-memory secret for this process.");
            return generatedSecret;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(configuredSecret);
            if (decoded.length >= MIN_SECRET_LENGTH_BYTES) {
                return decoded;
            }
        } catch (IllegalArgumentException ignored) {
            // Fall back to raw bytes below when the configured secret is not Base64.
        }

        byte[] rawBytes = configuredSecret.getBytes();
        if (rawBytes.length < MIN_SECRET_LENGTH_BYTES) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes or a Base64 value that decodes to 32 bytes.");
        }
        return rawBytes;
    }
}
