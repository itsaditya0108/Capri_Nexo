package com.example.authapp.dto;

public class UserSearchRequest {
    private String query;
    private int limit = 10;

    // getters & setters

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
