package com.example.authapp.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "CF-Connecting-IP",
            "True-Client-IP"
    };

    public static String getClientIp(HttpServletRequest request) {

        String ip = null;

        // 1️⃣ Check proxy headers
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);
            if (value != null && !value.isBlank() && !"unknown".equalsIgnoreCase(value)) {
                ip = value.split(",")[0].trim(); // take first IP
                break;
            }
        }

        // 2️⃣ Fallback
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }

        // 3️⃣ Normalize IPv6 localhost
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }
}
