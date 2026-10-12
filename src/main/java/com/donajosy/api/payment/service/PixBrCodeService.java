package com.donajosy.api.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class PixBrCodeService {
    private static final String PIX_GUI = "br.gov.bcb.pix";
    private static final String CURRENCY_BRL = "986";

    private final String pixKey;
    private final String merchantName;
    private final String merchantCity;

    public PixBrCodeService(
            @Value("${pix.key:}") String pixKey,
            @Value("${pix.merchant-name:}") String merchantName,
            @Value("${pix.merchant-city:}") String merchantCity
    ) {
        this.pixKey = pixKey;
        this.merchantName = merchantName;
        this.merchantCity = merchantCity;
    }

    public String build(BigDecimal amount, String txid) {
        if (isBlank(pixKey) || isBlank(merchantName) || isBlank(merchantCity)) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Configure PIX_KEY, PIX_MERCHANT_NAME e PIX_MERCHANT_CITY no .env"
            );
        }
        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pix total must be greater than zero");
        }

        String key = pixKey.trim();
        if (key.getBytes(StandardCharsets.UTF_8).length > 77) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "PIX_KEY exceeds the Pix limit of 77 bytes");
        }
        String name = normalize(merchantName, 25, true);
        String city = normalize(merchantCity, 15, true);
        String reference = normalize(txid, 25, false);
        if (reference.isEmpty()) {
            reference = "***";
        }

        String merchantAccount = tlv("00", PIX_GUI) + tlv("01", key);
        String amountText = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        String payload = tlv("00", "01")
                + tlv("01", "11")
                + tlv("26", merchantAccount)
                + tlv("52", "0000")
                + tlv("53", CURRENCY_BRL)
                + tlv("54", amountText)
                + tlv("58", "BR")
                + tlv("59", name)
                + tlv("60", city)
                + tlv("62", tlv("05", reference))
                + "6304";

        return payload + crc16(payload);
    }

    private static String tlv(String id, String value) {
        int length = value.getBytes(StandardCharsets.UTF_8).length;
        if (length > 99) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pix field " + id + " exceeds 99 bytes");
        }
        return id + String.format(Locale.ROOT, "%02d", length) + value;
    }

    private static String normalize(String value, int maxBytes, boolean uppercase) {
        String normalized = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        if (uppercase) {
            normalized = normalized.toUpperCase(Locale.ROOT);
        }

        StringBuilder result = new StringBuilder();
        int bytes = 0;
        for (int offset = 0; offset < normalized.length();) {
            int codePoint = normalized.codePointAt(offset);
            String character = new String(Character.toChars(codePoint));
            int characterBytes = character.getBytes(StandardCharsets.UTF_8).length;
            if (bytes + characterBytes > maxBytes) {
                break;
            }
            result.append(character);
            bytes += characterBytes;
            offset += Character.charCount(codePoint);
        }
        return result.toString();
    }

    private static String crc16(String payload) {
        int crc = 0xFFFF;
        for (byte value : payload.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (value & 0xFF) << 8;
            for (int bit = 0; bit < 8; bit++) {
                crc = (crc & 0x8000) != 0
                        ? ((crc << 1) ^ 0x1021) & 0xFFFF
                        : (crc << 1) & 0xFFFF;
            }
        }
        return String.format(Locale.ROOT, "%04X", crc);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

