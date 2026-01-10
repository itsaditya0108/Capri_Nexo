package com.company.usermicroservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @Column(name = "business_id")
    private String id;   // UIDx

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String panno;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String mobile;

    private String city;

    private String status;

    @Column(name = "address_line1")
    private String line1;
    //address_line1 VARCHAR(255)

    @Column(name = "address_line2")
    private String line2;

    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    // ===== Getters & Setters =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPanno() { return panno; }
    public void setPanno(String panno) { this.panno = panno; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}
