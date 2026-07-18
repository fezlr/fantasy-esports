package com.fezlr.fantasy.telegram.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fezlr.fantasy.user.dto.request.TelegramUserData;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelegramUserData validateAndExtractUser(String initData) {
        Map<String, String> params = parse(initData);
        String receivedHash = params.remove("hash");
        if (receivedHash == null) {
            throw new SecurityException("Missing hash in initData");
        }

        String dataCheckString = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("\n"));

        String computedHash = calculateHash(dataCheckString);

        if (!computedHash.equals(receivedHash)) {
            throw new SecurityException("Invalid initData hash");
        }

        String userJson = params.get("user");
        if (userJson == null) {
            throw new SecurityException("Missing user in initData");
        }

        return parseUser(userJson);
    }

    private String calculateHash(String dataCheckString) {
        try {
            Mac secretMac = Mac.getInstance("HmacSHA256");
            secretMac.init(new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] secretKey = secretMac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

            Mac dataMac = Mac.getInstance("HmacSHA256");
            dataMac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hash = dataMac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> parse(String initData) {
        Map<String, String> result = new HashMap<>();
        for (String pair : initData.split("&")) {
            if (pair.isBlank()) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            result.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
        }
        return result;
    }

    private TelegramUserData parseUser(String userJson) {
        try {
            JsonNode node = objectMapper.readTree(userJson);
            Long id = node.get("id").asLong();
            String username = node.hasNonNull("username") ? node.get("username").asText() : null;
            return new TelegramUserData(id, username);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
