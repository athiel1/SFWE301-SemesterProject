package Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static InventoryService inventoryService;
    private static Scanner scanner;
    private static final int LOW_STOCK_THRESHOLD = 120;

    public static void main(String[] args) {
        inventoryService = new InventoryService();
        scanner = new Scanner(System.in);

        // Load inventory using InventoryApp
        InventoryApp.loadInventoryFromFile(inventoryService, "Inventory.txt");

        boolean running = true;
        while (running) {
            printMenu();
            String userInput = scanner.nextLine().toLowerCase().trim();

            if (userInput.equals("exit")) {
                running = false;
                continue;
            }

            actionTask(userInput);
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Pharmacy Management System ===");
        System.out.println("Available commands:");
        System.out.println("1. 'update supplier' - Update supplier information");
        System.out.println("2. 'update inventory' - Update inventory information");
        System.out.println("3. 'update stock' - Update stock levels");
        System.out.println("4. 'update expired' - Update expired medications");
        System.out.println("5. 'check low stock' - View low stock notifications");
        System.out.println("6. 'reorder' - Process automatic reorders");
        System.out.println("7. 'print inventory' - Display complete inventory");
        System.out.println("8. 'exit' - Exit the system");
        System.out.print("Enter command: ");
    }
    	    

    private static void actionTask(String userInput) {
        switch (userInput) {
            case "update supplier":
                updateSupplier();
                break;
            case "update inventory":
                updateInventory();
                break;
            case "update stock":
                updateStock();
                break;
            case "update expired":
                updateExpiredMeds();
                break;
            case "check low stock":
                checkLowStock();
                break;
            case "reorder":
                processReorder();
                break;
            case "print inventory":
                printInventory();
                break;
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }
    private static void printInventory() {
        System.out.println("\n====== Complete Inventory List ======");
        List<InventoryItem> items = inventoryService.getAllItems();
        
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        // Print header
        System.out.println(String.format("%-5s %-20s %-10s %-10s %-10s %-12s %-8s %-20s %-20s %-15s",
            "ID", "Name", "Quantity", "Price($)", "Sold", "Expiry Date", "Control", "Supplier", "Allergens", "Total Value($)"));
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        // Print each item
        for (InventoryItem item : items) {
            System.out.println(String.format("%-5d %-20s %-10d %-10.2f %-10d %-12s %-8b %-20s %-20s %-15.2f",
                item.getId(),
                truncateString(item.getName(), 20),
                item.getQuantity(),
                item.getPrice(),
                item.getAmountSold(),
                item.getExpDate(),
                item.isConSubstancePackage(),
                truncateString(item.getSupplier(), 20),
                truncateString(item.getAllergen().toString(), 20),
                item.calculateTotalValue()
            ));
        }
        // Print summary
        System.out.println("\nInventory Summary:");
        System.out.println("Total number of items: " + items.size());
        System.out.println("Total inventory value: $" + 
            String.format("%.2f", items.stream().mapToDouble(InventoryItem::calculateTotalValue).sum()));
    }

    // Helper method to truncate long strings
    private static String truncateString(String str, int length) {
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length - 3) + "...";
    }
    private static void updateSupplier() {
        System.out.println("\n=== Update Supplier ===");
        System.out.print("Enter item ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new supplier name: ");
        String newSupplier = scanner.nextLine();

        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getId() == id) {
                item.setSupplier(newSupplier);
                System.out.println("Supplier updated successfully!");
                return;
            }
        }
        System.out.println("Item not found!");
    }

    private static void updateInventory() {
        System.out.println("\n=== Update Inventory ===");
        System.out.print("Enter item ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getId() == id) {
                System.out.println("Current item details: " + item);
                System.out.print("Enter new price (or press enter to skip): ");
                String priceInput = scanner.nextLine();
                if (!priceInput.isEmpty()) {
                    item.setPrice(Double.parseDouble(priceInput));
                }

                System.out.print("Enter new expiry date (or press enter to skip): ");
                String dateInput = scanner.nextLine();
                if (!dateInput.isEmpty()) {
                    item.setExpDate(dateInput);
                }

                System.out.println("Inventory updated successfully!");
                return;
            }
        }
        System.out.println("Item not found!");
    }

    private static void updateStock() {
        System.out.println("\n=== Update Stock ===");
        System.out.print("Enter item ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter quantity change (positive for addition, negative for reduction): ");
        int changeAmount = Integer.parseInt(scanner.nextLine());

        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getId() == id) {
                item.updateQuantity(changeAmount);
                System.out.println("Stock updated successfully! New quantity: " + item.getQuantity());
                return;
            }
        }
        System.out.println("Item not found!");
    }

    private static void updateExpiredMeds() {
        System.out.println("\n=== Update Expired Medications ===");
        System.out.print("Enter item ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter new expiry date (YYYY-MM-DD): ");
        String newDate = scanner.nextLine();

        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getId() == id) {
                item.setExpDate(newDate);
                System.out.println("Expiry date updated successfully!");
                return;
            }
        }
        System.out.println("Item not found!");
    }

    private static void checkLowStock() {
        System.out.println("\n=== Low Stock Items ===");
        boolean foundLowStock = false;

        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
                System.out.println("Low stock alert: " + item.getName() + 
                                 " (ID: " + item.getId() + 
                                 ") - Current quantity: " + item.getQuantity());
                foundLowStock = true;
            }
        }

        if (!foundLowStock) {
            System.out.println("No items are currently low in stock.");
        }
    }

    private static void processReorder() {
        System.out.println("\n=== Process Reorders ===");
        List<InventoryItem> lowStockItems = new ArrayList<>();

        // Collect all low stock items
        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
                lowStockItems.add(item);
                System.out.println("Item: " + item.getName() + 
                                 " (ID: " + item.getId() + 
                                 ") - Current quantity: " + item.getQuantity());
            }
        }

        if (lowStockItems.isEmpty()) {
            System.out.println("No items need reordering.");
            return;
        }

        System.out.print("Would you like to place orders for these items? (yes/no): ");
        String response = scanner.nextLine().toLowerCase();

        if (response.equals("yes")) {
            for (InventoryItem item : lowStockItems) {
                int orderQuantity = LOW_STOCK_THRESHOLD - item.getQuantity();
                System.out.println("Ordering " + orderQuantity + " units of " + item.getName() + 
                                 " from supplier: " + item.getSupplier());
            }
            System.out.println("Orders processed successfully!");
        } else {
            System.out.println("Reorder cancelled.");
        }
    }
}
