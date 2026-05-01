package org.example.medi;

public class Order {
    private String buyerEmail;
    private String sellerEmail;
    private String productName;
    private int quantity;
    private double price;
    private String status;
    private String orderDate;

    public Order(String buyerEmail, String sellerEmail, String productName,
                 int quantity, double price, String status, String orderDate) {
        this.buyerEmail = buyerEmail;
        this.sellerEmail = sellerEmail;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.orderDate = orderDate;
    }

    // Getters and setters for all fields
    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}