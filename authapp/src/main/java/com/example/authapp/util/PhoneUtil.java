package com.example.authapp.util;

public class PhoneUtil {

    private static final String DEFAULT_COUNTRY_CODE = "+91";

    public static String normalizeIndianPhone(String phone) {

        if (phone == null) {
            throw new IllegalArgumentException("Phone cannot be null");
        }

        phone = phone.trim();

        // Already normalized
        if (phone.startsWith("+91")) {
            return phone;
        }

        // Remove accidental spaces or dashes
        phone = phone.replaceAll("[^0-9]", "");

        // Must be 10 digits
        if (phone.length() != 10) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        return DEFAULT_COUNTRY_CODE + phone;
    }
}
