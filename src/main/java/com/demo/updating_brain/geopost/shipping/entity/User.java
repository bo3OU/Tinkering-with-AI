package com.demo.updating_brain.geopost.shipping.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramChannel;
    private String fullname;

    // Constructors
    public User() {
    }

    public User(String telegramChannel, String fullname) {
        this.telegramChannel = telegramChannel;
        this.fullname = fullname;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelegramChannel() {
        return telegramChannel;
    }

    public void setTelegramChannel(String telegramChannel) {
        this.telegramChannel = telegramChannel;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
