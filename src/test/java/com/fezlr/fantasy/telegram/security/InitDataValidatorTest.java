package com.fezlr.fantasy.telegram.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class InitDataValidatorTest {
    private static final String BOT_TOKEN = "123456:TEST-BOT-TOKEN";
    private InitDataValidator initDataValidator;

    @BeforeEach
    void setUp() {
        initDataValidator = new InitDataValidator();
        ReflectionTestUtils.setField(initDataValidator, "botToken", BOT_TOKEN);
    }

    @Test
    void shouldExtractUserId_whenHashIsValid() {
        String userJson = "{\"id\":123456789,\"first_name\":\"Test\"}";
        String authDate = "1700000000";
        String initData = buildValidInitData(userJson, authDate);

        Long userId = initDataValidator.validateAndExtractUserId(initData);

        assertThat(userId).isEqualTo(123456789L);
    }

    @Test
    void shouldThrow_whenHashIsInvalid() {
        String userJson = "{\"id\":123456789,\"first_name\":\"Test\"}";
        String tamperedInitData = "user=" + encode(userJson) + "&auth_date=1700000000&hash=deadbeef";

        assertThatThrownBy(() -> initDataValidator.validateAndExtractUserId(tamperedInitData))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void shouldThrow_whenHashIsMissing() {
        String initDataWithoutHash = "user=" + encode("{\"id\":1}") + "&auth_date=1700000000";

        assertThatThrownBy(() -> initDataValidator.validateAndExtractUserId(initDataWithoutHash))
                .isInstanceOf(SecurityException.class);
    }

    private String buildValidInitData(String userJson, String authDate) {
        try {
            Map<String, String> params = new TreeMap<>();
            params.put("user", userJson);
            params.put("auth_date", authDate);

            String dataCheckString = params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            Mac secretMac = Mac.getInstance("HmacSHA256");
            secretMac.init(new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] secretKey = secretMac.doFinal(BOT_TOKEN.getBytes(StandardCharsets.UTF_8));

            Mac dataMac = Mac.getInstance("HmacSHA256");
            dataMac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hash = dataMac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            String hashHex = HexFormat.of().formatHex(hash);

            return "user=" + encode(userJson) + "&auth_date=" + authDate + "&hash=" + hashHex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
