package com.company.rank_microservice.dto;

public class RankResponse {

    private String userId;
    private String rank;

    public RankResponse(String userId, String rank) {
        this.userId = userId;
        this.rank = rank;
    }

    // getters & setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
