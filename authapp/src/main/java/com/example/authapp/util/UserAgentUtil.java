package com.example.authapp.util;

import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentUtil {

    private static final Parser parser = new Parser();

    public static ParsedUserAgent parse(String userAgent) {

        if (userAgent == null || userAgent.isBlank()) {
            return ParsedUserAgent.unknown();
        }

        Client client = parser.parse(userAgent);

        String browser = client.userAgent.family;
        String os = client.os.family;
        String device = detectDeviceType(os, browser);

        return new ParsedUserAgent(browser, os, device);
    }

    private static String detectDeviceType(String os, String browser) {
        if (os == null) return "UNKNOWN";

        String osLower = os.toLowerCase();

        if (osLower.contains("android") || osLower.contains("ios")) {
            return "MOBILE";
        }
        if (osLower.contains("ipad") || osLower.contains("tablet")) {
            return "TABLET";
        }
        return "DESKTOP";
    }
}
