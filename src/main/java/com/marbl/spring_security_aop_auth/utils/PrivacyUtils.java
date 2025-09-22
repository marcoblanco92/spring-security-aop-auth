package com.marbl.spring_security_aop_auth.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrivacyUtils {

    /**
     * Masks the sensitive part of an email for safe logging.
     * Example: "marco@example.com" -> "m***o@example.com"
     *
     * @param email the email to mask
     * @return masked email
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "invalid email";

        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        String maskedName;
        if (name.length() <= 2) {
            maskedName = "**"; // short names are fully masked
        } else {
            maskedName = name.charAt(0) 
                    + "*".repeat(name.length() - 2)  // mask middle characters
                    + name.charAt(name.length() - 1); // keep first and last
        }

        return maskedName + "@" + domain;
    }
}