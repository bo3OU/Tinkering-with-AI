package com.demo.updating_brain.geopost.shipping.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String locationCity;

    private String destination;

    private LocalDate estimationDate;

    // Constructors
    public Order() {
    }

    public Order(Long userId) {
        this.userId = userId;
        this.status = OrderStatus.PENDING; // Default status
    }

    public Order(Long userId, OrderStatus status, String locationCity) {
        this.userId = userId;
        this.status = status;
        this.locationCity = locationCity;
    }

    public Order(Long userId, OrderStatus status, String locationCity, String destination, LocalDate estimationDate) {
        this.userId = userId;
        this.status = status;
        this.locationCity = locationCity;
        this.destination = destination;
        this.estimationDate = estimationDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getEstimationDate() {
        return estimationDate;
    }

    public void setEstimationDate(LocalDate estimationDate) {
        this.estimationDate = estimationDate;
    }
}
