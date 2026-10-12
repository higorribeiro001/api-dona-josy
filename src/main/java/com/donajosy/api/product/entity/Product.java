package com.donajosy.api.product.entity;

import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(unique=true, updatable = false, nullable=false)
    private UUID id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Column(nullable=true)
    private String image;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private boolean isActive = true;

    public Product(String name, String description, ProductCategory category, String image, BigDecimal price, boolean isActive) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = image;
        this.price = price;
        this.isActive = isActive;
    }

    public Product() {
        // construct empty
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
