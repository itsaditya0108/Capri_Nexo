package com.example.authapp.util;

public class ParsedUserAgent {

    private final String browser;
    private final String os;
    private final String deviceType;

    public ParsedUserAgent(String browser, String os, String deviceType) {
        this.browser = browser;
        this.os = os;
        this.deviceType = deviceType;
    }

    public static ParsedUserAgent unknown() {
        return new ParsedUserAgent("Unknown", "Unknown", "Unknown");
    }

    public String getBrowser() {
        return browser;
    }

    public String getOs() {
        return os;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
