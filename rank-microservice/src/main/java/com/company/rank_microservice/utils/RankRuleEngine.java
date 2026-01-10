package com.company.rank_microservice.utils;

public class RankRuleEngine {

    public static String decideRank(String city) {
        if ("Mumbai".equalsIgnoreCase(city)) {
            return "GOLD";
        }
        return "SILVER";
    }
}
