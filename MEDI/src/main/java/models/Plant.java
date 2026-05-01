package models;

public class Plant {
    private String name;
    private double price;
    private String description;
    private String imagePath;

    public Plant(String name, double price, String description, String imagePath) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
}