package Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private List<InventoryItem> inventory = new ArrayList<>();
    private int nextId = 1; // ID generator for new items

    // Add a new item to inventory
    public void addItem(String name, int quantity, double price, int amountSold, String expDate, List<String> allergen, boolean conSubstancePackage) {
        InventoryItem item = new InventoryItem(nextId++, name, quantity, price, amountSold, expDate, allergen, conSubstancePackage);
        inventory.add(item);
    }

    // Retrieve all inventory items
    public List<InventoryItem> getAllItems() {
        return new ArrayList<>(inventory); // Return a copy of the list to protect original data
    }

    // Update an existing item by ID
    public boolean updateItem(int id, int quantity, double price, int amountSold, String expDate, List<String> allergen, boolean conSubstancePackage) {
        for (InventoryItem item : inventory) {
            if (item.getId() == id) {
                item.setQuantity(quantity);
                item.setPrice(price);
                item.setAmountSold(amountSold);
                item.setExpDate(expDate);
                item.setAllergen(allergen);
                item.setConSubstancePackage(conSubstancePackage);
                return true; // Item found and updated
            }
        }
        return false; // Item not found
    }

    // Delete an item from inventory by ID
    public boolean deleteItem(int id) {
        return inventory.removeIf(item -> item.getId() == id); // Item found and deleted
    }
}
