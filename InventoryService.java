package Inventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private List<InventoryItem> inventory = new ArrayList<>();
    private int nextId = 1;

    // addItem method that uses CSV provided ID
    public void addItem(int id, String name, int quantity, double price, int amountSold, 
                       String expDate, boolean conSubstancePackage, String supplier, List<String> allergen) {
        InventoryItem item = new InventoryItem(id, name, quantity, price, amountSold, 
                                             expDate, conSubstancePackage, supplier, allergen);
        inventory.add(item);
        // Update nextId if the current id is larger
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // Another method for adding items without providing an ID (will use nextId)
    public void addNewItem(String name, int quantity, double price, int amountSold, 
                          String expDate, boolean conSubstancePackage, String supplier, List<String> allergen) {
        InventoryItem item = new InventoryItem(nextId++, name, quantity, price, amountSold, 
                                             expDate, conSubstancePackage, supplier, allergen);
        inventory.add(item);
    }

    // Retrieve all inventory items
    public List<InventoryItem> getAllItems() {
        return new ArrayList<>(inventory);
    }

    // Update an existing item by ID
    public boolean updateItem(int id, int quantity, double price, int amountSold, 
                            String expDate, boolean conSubstancePackage, String supplier, List<String> allergen) {
        for (InventoryItem item : inventory) {
            if (item.getId() == id) {
                item.setQuantity(quantity);
                item.setPrice(price);
                item.setAmountSold(amountSold);
                item.setExpDate(expDate);
                item.setConSubstancePackage(conSubstancePackage);
                item.setSupplier(supplier);
                item.setAllergen(allergen);
                return true;
            }
        }
        return false;
    }
    //Reorder expired medicine with new expiration dates.
    public void reorderExpiredItem(InventoryItem item, int quantity) {
        item.updateQuantity(quantity);
        // Increment expiration date by one year
        LocalDate currentExpiry = LocalDate.parse(item.getExpDate());
        item.setExpDate(currentExpiry.plusYears(1).toString());
    }

    // Delete an item from inventory by ID
    public boolean deleteItem(int id) {
        return inventory.removeIf(item -> item.getId() == id);
    }

    // Get current nextId value (useful for testing)
    public int getNextId() {
        return nextId;
    }
}
