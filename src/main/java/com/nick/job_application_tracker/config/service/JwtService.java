package com.nick.job_application_tracker.config.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {

    public String[] validateTokenAndGetUsernameAndRole(String token) throws Exception {
        String[] split = token.split("\\.");
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

        System.out.println("issuer: "+issuer +
                (!issuer.equals("Oauth2applicationApplication")?" hence it is not trusted":" hence it is trusted"));

        return !issuer.equals("Oauth2applicationApplication");
    }

    private boolean isTokenExpired(String decodedPayload) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode payloadJson = objectMapper.readTree(decodedPayload);

        long issueTime = payloadJson.get("iat").asLong();
        long expiryTime = payloadJson.get("exp").asLong();

        long timeInSeconds = System.currentTimeMillis()/1000;
        boolean isTokenExpired = !(issueTime < timeInSeconds && timeInSeconds < expiryTime);
        System.out.println(timeInSeconds + " ? "+issueTime+" ? "+expiryTime + " thus " + isTokenExpired);

        return isTokenExpired;
    }

    private boolean isTokenTampered(String message, String signatureToMatchWith) throws NoSuchAlgorithmException, InvalidKeyException {
        String signature = generateSignature(message);
        System.out.println("Signature1: "+signature +
                "\nSignature2: " + signatureToMatchWith.replaceAll("=+$", "") + " thus "+(!signature.equals(signatureToMatchWith.replaceAll("=+$", ""))));
        return !signature.equals(signatureToMatchWith.replaceAll("=+$", ""));
    }

    //TODO instance could be based on header values
    private String generateSignature(String message) throws InvalidKeyException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA256");
        String secret = "LzjjrDyx48aZ0VX0IjP6Igr0zO+lmzUWadqi7DaCDd8=";
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
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
                "  \"iss\": \"Oauth2applicationApplication\",\n" +
                "  \"sub\": \""+username+"\",\n" +
                "  \"role\": \""+role+"\",\n" +
                "  \"iat\": "+ timeInSeconds +",\n" +
                "  \"exp\": "+(timeInSeconds+3600)+"\n" +
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
    
}
