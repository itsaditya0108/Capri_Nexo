package com.company.usermicroservice.dto;

public class RankResponse {
    private String userId;
    private String rank;
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
