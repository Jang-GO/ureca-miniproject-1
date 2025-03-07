package domain;

public class ShopPhoneDTO {
    private String modelName;
    private String brand;
    private int price;
    private int stock;

    public ShopPhoneDTO(String modelName, String brand, int price, int stock) {
        this.modelName = modelName;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    // Getter 추가
    public String getModelName() { return modelName; }
    public String getBrand() { return brand; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
}
