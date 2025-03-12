package domain;

import java.time.LocalDate;

public class Phone {
    private int phoneId;
    private String modelName;
    private String brand;
    private int price;
    private LocalDate createdAt;

    public Phone() {
    }

    public Phone(String brand, LocalDate createdAt, String modelName, int phoneId, int price) {
        this.brand = brand;
        this.createdAt = createdAt;
        this.modelName = modelName;
        this.phoneId = phoneId;
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
