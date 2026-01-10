package com.company.usermicroservice.dto;

public class UserRequest {

    private String userId;
    private String name;
    private String panno;
    private String email;
    private String mobile;
    private String city;
    private String status;
    private Address address;
    private String profileImageBase64;

    // ===== Getters & Setters =====

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPanno() {
        return panno;
    }

    public void setPanno(String panno) {
        this.panno = panno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }
}
