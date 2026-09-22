package com.klu.api_gateway.security;

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

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

            return objectMapper.readTree(
                    new String(decoded, StandardCharsets.UTF_8));

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

            String expectedSignature = sign(
                    dataToSign,
                    secretKey);

            return expectedSignature.equals(parts[2]);

        } catch (Exception e) {
            return false;
        }
    }

    private String sign(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");

        mac.init(secretKeySpec);

        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(rawHmac);
    }
}