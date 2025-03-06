package domain;

public class Customer {
    private int CustomerId;
    private String name;
    private String address;

    public Customer() {
    }

    public Customer(String address, int customerId, String name) {
        this.address = address;
        CustomerId = customerId;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
