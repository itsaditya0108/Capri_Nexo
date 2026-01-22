package com.example.authapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_status")
public class UserStatus {

    @Id
    @Column(name = "user_status_id", length = 2)
    private String id;

    @Column(name = "name", length = 50)
    private String name;

    // getters


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
