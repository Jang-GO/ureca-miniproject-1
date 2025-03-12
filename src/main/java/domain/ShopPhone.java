package domain;

public class ShopPhone {
    private int shopId;
    private int phoneId;
    private int stock;

    public ShopPhone() {
    }

    public ShopPhone(int stock, int shopId, int phoneId) {
        this.stock = stock;
        this.shopId = shopId;
        this.phoneId = phoneId;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
