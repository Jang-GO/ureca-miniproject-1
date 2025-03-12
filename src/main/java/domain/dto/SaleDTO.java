package domain.dto;

import java.time.LocalDateTime;

public class SaleDTO {
    private String customerName;
    private String modelName;
    private String customerPhoneNumber;
    private int quantity;

    public SaleDTO(String customerName, String customerPhoneNumber, String modelName, int quantity, LocalDateTime saleDate, int totalPrice) {
        this.customerName = customerName;
        this.modelName = modelName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.totalPrice = totalPrice;
    }

    private int totalPrice;
    private LocalDateTime saleDate;

    public SaleDTO(String customerName, String modelName, int quantity, int totalPrice, LocalDateTime saleDate) {
        this.customerName = customerName;
        this.modelName = modelName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.saleDate = saleDate;
    }

    // Getter 추가
    public String getCustomerName() { return customerName; }
    public String getModelName() { return modelName; }
    public int getQuantity() { return quantity; }
    public int getTotalPrice() { return totalPrice; }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public LocalDateTime getSaleDate() { return saleDate; }
}

