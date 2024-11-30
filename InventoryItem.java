package Inventory;

import java.util.List;

public class InventoryItem {

    private int id;
    private String name;
    private int quantity;
    private double price;
    private int amountSold;
    private String expDate;
    private List<String> allergen;
    private boolean conSubstancePackage;

    // Constructors
    public InventoryItem(int id, String name, int quantity, double price, int amountSold, String expDate, List<String> allergen, boolean conSubstancePackage) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.amountSold = amountSold;
        this.expDate = expDate;
        this.allergen = allergen;
        this.conSubstancePackage = conSubstancePackage;
    }
    // Getter and Setter for name
    public String getName() {
        return name;
    }
    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and Setter for amountSold
    public int getAmountSold() {
        return amountSold;
    }

    public void setAmountSold(int amountSold) {
        this.amountSold = amountSold;
    }

    // Getter and Setter for expDate
    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    // Getter and Setter for allergen
    public List<String> getAllergen() {
        return allergen;
    }

    public void setAllergen(List<String> allergen) {
        this.allergen = allergen;
    }

    // Getter and Setter for conSubstancePackage
    public boolean isConSubstancePackage() {
        return conSubstancePackage;
    }

    public void setConSubstancePackage(boolean conSubstancePackage) {
        this.conSubstancePackage = conSubstancePackage;
    }

    // Method to update quantity
    public void updateQuantity(int amount) {
        this.quantity += amount;
    }

    // Method to calculate total value of item stock
    public double calculateTotalValue() {
        return this.quantity * this.price;
    }

    // toString method for easy printing
    @Override
    public String toString() {
        return "InventoryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amountSold=" + amountSold +
                ", expDate='" + expDate + '\'' +
                ", allergen=" + allergen +
                ", conSubstancePackage=" + conSubstancePackage +
                ", totalValue=" + calculateTotalValue() +
                '}';
    }
}