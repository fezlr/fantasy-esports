package com.fezlr.fantasy.telegram.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InitDataValidator {
    @Value("${telegram.bot.token}")
    private String botToken;

    public Long validateAndExtractUserId(String initData) {
        Map<String, String> params = parse(initData);
        String receivedHash = params.remove("hash");
        if (receivedHash == null) {
            throw new SecurityException("Missing hash in initData");
        }

        String dataCheckString = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac secretMac = Mac.getInstance("HmacSHA256");
            secretMac.init(secretKeySpec);
            byte[] secretKey = secretMac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

            Mac dataMac = Mac.getInstance("HmacSHA256");
            dataMac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] computedHash = dataMac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            String computedHashHex = HexFormat.of().formatHex(computedHash);

            if (!computedHashHex.equals(receivedHash)) {
                throw new SecurityException("Invalid initData hash");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        String userJson = params.get("user");
        if (userJson == null) {
            throw new SecurityException("Missing user in initData");
        }

        return extractUserId(userJson);
    }

    private Map<String, String> parse(String initData) {
        Map<String, String> result = new HashMap<>();
        for (String pair : initData.split("&")) {
            String[] kv = pair.split("=", 2);
            result.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }
        return result;
    }

    private Long extractUserId(String userJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(userJson);
            return node.get("id").asLong();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
