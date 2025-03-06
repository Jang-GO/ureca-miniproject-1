package domain;

import java.time.LocalDateTime;

public class Sale {
    private int sale_id;
    private int quantity;
    private int totalPrice;
    private LocalDateTime saleDate;
    private int customerId;
    private int ShopId;
    private int PhoneId;


    public Sale() {
    }

    public Sale(int customerId, int phoneId, int quantity, int sale_id, LocalDateTime saleDate, int shopId, int totalPrice) {
        this.customerId = customerId;
        PhoneId = phoneId;
        this.quantity = quantity;
        this.sale_id = sale_id;
        this.saleDate = saleDate;
        ShopId = shopId;
        this.totalPrice = totalPrice;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPhoneId() {
        return PhoneId;
    }

    public void setPhoneId(int phoneId) {
        PhoneId = phoneId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setShopId(int shopId) {
        ShopId = shopId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
