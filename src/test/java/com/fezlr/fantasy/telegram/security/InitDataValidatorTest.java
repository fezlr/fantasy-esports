package com.fezlr.fantasy.telegram.security;

import com.fezlr.fantasy.user.dto.request.TelegramUserData;
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

class InitDataValidatorTest {
    private static final String BOT_TOKEN = "123456:TEST-BOT-TOKEN-FOR-UNIT-TESTS";
    private InitDataValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InitDataValidator();
        ReflectionTestUtils.setField(validator, "botToken", BOT_TOKEN);
    }

    @Test
    void shouldExtractUser_whenHashIsValid() {
        String userJson = "{\"id\":123456789,\"username\":\"test_user\"}";
        String authDate = "1700000000";
        String initData = buildValidInitData(userJson, authDate);

        TelegramUserData result = validator.validateAndExtractUser(initData);

        assertThat(result.telegramId()).isEqualTo(123456789L);
        assertThat(result.telegramUsername()).isEqualTo("test_user");
    }

    @Test
    void shouldExtractUserWithNullUsername_whenUsernameNotPresent() {
        String userJson = "{\"id\":123456789,\"first_name\":\"Ivan\"}";
        String authDate = "1700000000";
        String initData = buildValidInitData(userJson, authDate);

        TelegramUserData result = validator.validateAndExtractUser(initData);

        assertThat(result.telegramId()).isEqualTo(123456789L);
        assertThat(result.telegramUsername()).isNull();
    }

    @Test
    void shouldThrow_whenHashIsInvalid() {
        String userJson = "{\"id\":123456789,\"username\":\"test_user\"}";
        String tamperedInitData = "user=" + encode(userJson) + "&auth_date=1700000000&hash=deadbeef";

        assertThatThrownBy(() -> validator.validateAndExtractUser(tamperedInitData))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Invalid initData hash");
    }

    @Test
    void shouldThrow_whenHashIsMissing() {
        String initDataWithoutHash = "user=" + encode("{\"id\":1}") + "&auth_date=1700000000";

        assertThatThrownBy(() -> validator.validateAndExtractUser(initDataWithoutHash))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Missing hash");
    }

    @Test
    void shouldThrow_whenUserFieldIsMissing() {
        String authDate = "1700000000";
        Map<String, String> params = new TreeMap<>();
        params.put("auth_date", authDate);
        String initData = signParams(params) ;

        assertThatThrownBy(() -> validator.validateAndExtractUser(initData))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Missing user");
    }

    @Test
    void shouldNotThrow_whenInitDataContainsMalformedPair() {
        String userJson = "{\"id\":123456789,\"username\":\"test_user\"}";
        String authDate = "1700000000";
        String validPart = buildValidInitData(userJson, authDate);
        String withMalformedPair = validPart + "&malformed_fragment_without_equals";

        TelegramUserData result = validator.validateAndExtractUser(withMalformedPair);

        assertThat(result.telegramId()).isEqualTo(123456789L);
    }

    private String buildValidInitData(String userJson, String authDate) {
        Map<String, String> params = new TreeMap<>();
        params.put("user", userJson);
        params.put("auth_date", authDate);
        return signParams(params);
    }

    private String signParams(Map<String, String> params) {
        try {
            String dataCheckString = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            Mac secretMac = Mac.getInstance("HmacSHA256");
            secretMac.init(new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] secretKey = secretMac.doFinal(BOT_TOKEN.getBytes(StandardCharsets.UTF_8));

            Mac dataMac = Mac.getInstance("HmacSHA256");
            dataMac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            byte[] hash = dataMac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

            String hashHex = HexFormat.of().formatHex(hash);

            String query = params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + encode(e.getValue()))
                    .collect(Collectors.joining("&"));

            return query + "&hash=" + hashHex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}