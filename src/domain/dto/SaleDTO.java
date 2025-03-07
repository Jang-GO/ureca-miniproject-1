package domain.dto;

import java.time.LocalDateTime;

public class SaleDTO {
    private String customerName;
    private String modelName;
    private int quantity;
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
    public LocalDateTime getSaleDate() { return saleDate; }
}

