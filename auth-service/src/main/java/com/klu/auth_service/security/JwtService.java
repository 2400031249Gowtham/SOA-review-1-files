package com.klu.auth_service.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret:swift_dispatch_logistics_jwt_super_secret_key_2026_klu_soa_security_key_must_be_long_enough}")
    private String secretKey;

    @Value("${jwt.expiration:86400}") // 24 hours in seconds
    private long expirationSeconds;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateToken(String email, Long userId, String role) {
        try {
            long now = Instant.now().getEpochSecond();
            long exp = now + expirationSeconds;

            // Header
            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put("alg", "HS256");
            headerMap.put("typ", "JWT");
            String headerJson = objectMapper.writeValueAsString(headerMap);
            String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));

            // Payload
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("sub", email);
            payloadMap.put("userId", userId);
            payloadMap.put("role", role);
            payloadMap.put("iat", now);
            payloadMap.put("exp", exp);
            String payloadJson = objectMapper.writeValueAsString(payloadMap);
            String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Signature
            String dataToSign = encodedHeader + "." + encodedPayload;
            String signature = sign(dataToSign, secretKey);

            return dataToSign + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public boolean isTokenValid(String token, String userEmail) {
        try {
            String email = extractUsername(token);
            return email != null && email.equalsIgnoreCase(userEmail) && !isTokenExpired(token) && verifySignature(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token) && verifySignature(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        JsonNode payload = extractPayload(token);
        if (payload != null && payload.has("sub")) {
            return payload.get("sub").asText();
        }
        return null;
    }

    public Long extractUserId(String token) {
        JsonNode payload = extractPayload(token);
        if (payload != null && payload.has("userId")) {
            return payload.get("userId").asLong();
        }
        return null;
    }

    public String extractRole(String token) {
        JsonNode payload = extractPayload(token);
        if (payload != null && payload.has("role")) {
            return payload.get("role").asText();
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        JsonNode payload = extractPayload(token);
        if (payload != null && payload.has("exp")) {
            long exp = payload.get("exp").asLong();
            return Instant.now().getEpochSecond() > exp;
        }
        return true;
    }

    private JsonNode extractPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readTree(new String(decoded, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean verifySignature(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            String dataToSign = parts[0] + "." + parts[1];
            String expectedSignature = sign(dataToSign, secretKey);
            return expectedSignature.equals(parts[2]);
        } catch (Exception e) {
            return false;
        }
    }

    private String sign(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }
}
