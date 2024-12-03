package Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;


public class Main {
    private static InventoryService inventoryService;
    private static Scanner scanner;
    private static final int LOW_STOCK_THRESHOLD = 120;
    private static StockForecasting stockForecasting;

    public static void main(String[] args) {
        inventoryService = new InventoryService();
        scanner = new Scanner(System.in);
        stockForecasting = new StockForecasting();
        // Load inventory using InventoryApp
        InventoryApp.loadInventoryFromFile(inventoryService, "Inventory.txt");
        // Load usage history for forecasting
        stockForecasting.loadUsageHistory();
        

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
        System.out.println("8. 'print suppliers' - Display all suppliers and supplied medications");
        System.out.println("9. 'expiring soon' - Display medications expiring within 30 days");
        System.out.println("10. 'expired meds' - Display and manage expired medications");
        System.out.println("11. 'view logs' - View system activity logs");    
        System.out.println("12. 'stock forecast' - View Low Stock Forecast based on previous sales trends");
        System.out.println("12. 'exit' - Exit the system");
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
            case "print suppliers":
                printSuppliers();
                break;
            case "expiring soon":
                expiringSoon();
                break;
            case "expired meds":
                expiredMeds();
                break;
            case "view logs":    // New case
                viewInventoryLogs();
                break;
            case "stock forecast":
            	viewLowStockForecast();
            	break;
            
            default:
                System.out.println("Invalid command. Please try again.");
        }
    }

    private static void expiringSoon() {
        System.out.println("\n=== Medications Expiring Soon ===");
        boolean foundExpiringSoon = false;
        LocalDate currentDate = LocalDate.now();

        for (InventoryItem item : inventoryService.getAllItems()) {
            LocalDate expiryDate = LocalDate.parse(item.getExpDate());
            if (!expiryDate.isBefore(currentDate) && expiryDate.isBefore(currentDate.plusDays(30))) {
                System.out.println("Expiring Soon: " + item.getName() +
                                   " (ID: " + item.getId() +
                                   ") - Expiry Date: " + expiryDate);
                foundExpiringSoon = true;
            }
        }

        if (!foundExpiringSoon) {
            System.out.println("No medications are expiring soon.");
        }
    }
    private static void expiredMeds() {
        System.out.println("\n=== Expired Medications ===");
        boolean foundExpired = false;
        LocalDate currentDate = LocalDate.now();

        List<InventoryItem> expiredItems = new ArrayList<>();

        // Find all expired medications
        for (InventoryItem item : inventoryService.getAllItems()) {
            LocalDate expiryDate = LocalDate.parse(item.getExpDate());
            if (expiryDate.isBefore(currentDate)) {
                expiredItems.add(item);
                foundExpired = true;
                System.out.println("Expired: " + item.getName() +
                                   " (ID: " + item.getId() +
                                   ") - Expiry Date: " + expiryDate +
                                   ", Stock: " + item.getQuantity());
            }
        }

        if (!foundExpired) {
            System.out.println("No expired medications found.");
            return;
        }

        // Ask user if they want to set all stock to 0
        System.out.print("Do you want to set the stock for all expired medications to 0? (yes/no): ");
        String response = scanner.nextLine().toLowerCase().trim();

        if (response.equals("yes")) {
            for (InventoryItem item : expiredItems) {
                item.updateQuantity(-item.getQuantity()); // Set stock to 0
                System.out.println("Stock set to 0 for: " + item.getName() + " (ID: " + item.getId() + ")");
            }
            System.out.println("Stock for all expired medications has been updated to 0.");
        } else {
            System.out.println("No changes made to expired medications.");
        }
    }


    private static void printSuppliers() {
    	// Initialize suppliers from inventory data
        Supplier.loadSuppliers(inventoryService);
        // Retrieve the list of all suppliers
        List<Supplier> suppliers = Supplier.getSuppliers();

        // Check if there are any suppliers
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found here.");
            return;
        }

        // Print supplier details
        System.out.println("List of Suppliers:");
        for (Supplier supplier : suppliers) {
            System.out.println("Supplier Name: " + supplier.getSupName());
            System.out.println("Medicines Supplied: " + String.join(", ", supplier.getMedicines()));
            System.out.println("--------------------------");
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
            "ID", "Name", "Quantity", "Price($)", "Sold", "Expiry Date", "Cont. Subs.", "   Supplier", "   Allergens", "Total Value($)"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------------");

        // Print each item
        for (InventoryItem item : items) {
            System.out.println(String.format("%-5d %-20s %-10d %-10.2f %-10d %-12s %-8b 	%-20s %-20s %-15.2f",
                item.getId(),
                item.getName(), 20,
                item.getQuantity(),
                item.getPrice(),
                item.getAmountSold(),
                item.getExpDate(),
                item.isConSubstancePackage(),
                item.getSupplier(), 20,
                item.getAllergen().toString(), 20,
                item.calculateTotalValue()
            ));
        }
        // Print summary
        System.out.println("\nInventory Summary:");
        System.out.println("Total number of items: " + items.size());
        System.out.println("Total inventory value: $" + 
            String.format("%.2f", items.stream().mapToDouble(InventoryItem::calculateTotalValue).sum()));
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
        System.out.println("\n=== Inventory Management ===");
        System.out.println("1. Make changes to existing item");
        System.out.println("2. Add new item");
        System.out.print("Enter your choice (1 or 2): ");
        
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                updateExistingItem();
                break;
            case "2":
                addNewInventoryItem();
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private static void updateExistingItem() {
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

    private static void addNewInventoryItem() {
        try {
            System.out.println("\n=== Add New Inventory Item ===");
            
            System.out.print("Enter medication name: ");
            String name = scanner.nextLine();

            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter price: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter amount sold: ");
            int amountSold = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter expiry date (YYYY-MM-DD): ");
            String expDate = scanner.nextLine();

            System.out.print("Is this a controlled substance? (true/false): ");
            boolean conSubstancePackage = Boolean.parseBoolean(scanner.nextLine());

            System.out.print("Enter supplier name: ");
            String supplier = scanner.nextLine();

            System.out.print("Enter allergens (separate with semicolons, or 'none'): ");
            String allergensInput = scanner.nextLine();
            List<String> allergens = new ArrayList<>();
            if (!allergensInput.equalsIgnoreCase("none")) {
                allergens = Arrays.asList(allergensInput.split(";"));
            }

            // Add the new item using the addNewItem method
            inventoryService.addNewItem(name, quantity, price, amountSold, 
                                      expDate, conSubstancePackage, supplier, allergens);

            System.out.println("New item added successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numeric values for quantity, price, and amount sold.");
        } catch (Exception e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
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
        
        for (InventoryItem item : inventoryService.getAllItems()) {
            if (item.getId() == id) {
                String oldDate = item.getExpDate();
                System.out.print("Enter new expiry date (YYYY-MM-DD) or 'remove' to remove expired stock: ");
                String input = scanner.nextLine();
                
                if (input.equalsIgnoreCase("remove")) {
                    int oldQuantity = item.getQuantity();
                    item.setQuantity(0);
                    
                    // Log the removal
                    LogManager.logChange(item, "EXPIRY_REMOVAL",
                        "Quantity: " + oldQuantity + ", Expiry: " + oldDate,
                        "REMOVED",
                        "SYSTEM");
                    
                    System.out.println("Expired medication removed from inventory.");
                } else {
                    item.setExpDate(input);
                    
                    // Log the expiry date update
                    LogManager.logChange(item, "EXPIRY_UPDATE",
                        oldDate,
                        input,
                        "SYSTEM");
                    
                    System.out.println("Expiry date updated successfully!");
                }
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
                int oldQuantity = item.getQuantity();
                
                // Update quantity
                item.updateQuantity(orderQuantity);
                
                // Log the reorder
                LogManager.logChange(item, "REORDER", 
                    String.valueOf(oldQuantity),
                    String.valueOf(item.getQuantity()),
                    "SYSTEM");
                
                System.out.println("Ordered " + orderQuantity + " units of " + item.getName() + 
                                 " from supplier: " + item.getSupplier());
            }
            System.out.println("Orders processed successfully!");
        } else {
            System.out.println("Reorder cancelled.");
        }
    }
    private static void viewInventoryLogs() {
        System.out.println("\n=== View Inventory Logs ===");
        System.out.println("1. View all logs");
        System.out.println("2. View stock updates");
        System.out.println("3. View price changes");
        System.out.println("4. View supplier changes");
        System.out.println("5. View expiry removals");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                LogManager.viewAllLogs();
                break;
            case "2":
                LogManager.viewLogsByType("STOCK_UPDATE");
                break;
            case "3":
                LogManager.viewLogsByType("PRICE_UPDATE");
                break;
            case "4":
                LogManager.viewLogsByType("SUPPLIER_UPDATE");
                break;
            case "5":
                LogManager.viewLogsByType("EXPIRY_REMOVAL");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }
    private static void viewLowStockForecast() {
        System.out.println("\n=== Low Stock Analysis ===");
        System.out.println("1. View current low stock items");
        System.out.println("2. View stock forecast report");
        System.out.print("Enter choice (1 or 2): ");
        
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                checkLowStock();
                break;
            case "2":
                stockForecasting.displayForecastForAllItems(inventoryService);
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

}
