package domain;

import java.time.LocalDate;

public class Shop {
    private int shopId;
    private String shopName;
    private String location;
    private LocalDate createdAt;
    private int ownerId;

    public Shop() {
    }

    public Shop(LocalDate createdAt, String location, int ownerId, int shopId, String shopName) {
        this.createdAt = createdAt;
        this.location = location;
        this.ownerId = ownerId;
        this.shopId = shopId;
        this.shopName = shopName;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
