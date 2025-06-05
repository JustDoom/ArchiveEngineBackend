package com.imjustdoom;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UrlUtil {
    public static String generateUrlHash(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(url.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    @NotNull
    public static String cleanUrl(String foundUrl) {
        String cleaned = foundUrl.replaceFirst("^https?://", "");
        int slashIndex = cleaned.indexOf('/');
        if (slashIndex != -1) {
            cleaned = cleaned.substring(0, slashIndex);
        }
        int questionIndex = cleaned.indexOf('?');
        if (questionIndex != -1) {
            cleaned = cleaned.substring(0, questionIndex);
        }

        int colonIndex = cleaned.lastIndexOf(':');
        if (colonIndex != -1 && colonIndex > cleaned.lastIndexOf(']')) {
            cleaned = cleaned.substring(0, colonIndex);
        }
        return cleaned;
    }
}
