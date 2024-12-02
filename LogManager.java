package Inventory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// InventoryLog class
class InventoryLog {
    private int logId;
    private LocalDateTime timestamp;
    private int itemId;
    private String itemName;
    private String changeType;  // REORDER, EXPIRY_REMOVAL, SUPPLIER_UPDATE, PRICE_UPDATE, STOCK_UPDATE
    private String oldValue;
    private String newValue;
    private String userInitiated;  // Who made the change
    
    public InventoryLog(int logId, int itemId, String itemName, String changeType, 
                       String oldValue, String newValue, String userInitiated) {
        this.logId = logId;
        this.timestamp = LocalDateTime.now();
        this.itemId = itemId;
        this.itemName = itemName;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userInitiated = userInitiated;
    }

    @Override
    public String toString() {
        return String.format("[%s] Item ID: %d, Name: %s - %s changed from '%s' to '%s' by %s",
            timestamp.toString(), itemId, itemName, changeType, oldValue, newValue, userInitiated);
    }

    // Getters
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getChangeType() { return changeType; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getUserInitiated() { return userInitiated; }
    public int getLogId() { return logId; }  // Moved getter here with other getters
}

// LogManager class
public class LogManager {
    private static List<InventoryLog> inventoryLogs = new ArrayList<>();
    private static int nextLogId = 1;
    private static final String LOG_FILE = "log.txt";
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logChange(InventoryItem item, String changeType, 
                               String oldValue, String newValue, String user) {
        // Create new log entry
        InventoryLog log = new InventoryLog(nextLogId++, item.getId(), item.getName(),
                                          changeType, oldValue, newValue, user);
        // Add to memory
        inventoryLogs.add(log);
        
        // Write to file
        writeToFile(log);
    }

    private static void writeToFile(InventoryLog log) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            // Format: [Timestamp] ItemID | ItemName | ChangeType | OldValue | NewValue | User
            out.println(String.format("[%s] ItemID: %d | %s | %s | From: %s | To: %s | By: %s",
                log.getTimestamp().format(formatter),
                log.getItemId(),
                log.getItemName(),
                log.getChangeType(),
                log.getOldValue(),
                log.getNewValue(),
                log.getUserInitiated()));
            
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }

    public static void viewAllLogs() {
        if (inventoryLogs.isEmpty()) {
            System.out.println("No logs found.");
            return;
        }

        System.out.println("\n=== Inventory Change Log ===");
        System.out.println(String.format("%-5s %-25s %-8s %-20s %-15s %-20s %-20s %-15s",
            "LogID", "Timestamp", "Item ID", "Item Name", "Change Type", "Old Value", "New Value", "User"));
        System.out.println("-".repeat(130));

        for (InventoryLog log : inventoryLogs) {
            System.out.println(String.format("%-5d %-25s %-8d %-20s %-15s %-20s %-20s %-15s",
                log.getLogId(),
                log.getTimestamp().format(formatter),
                log.getItemId(), 
                truncateString(log.getItemName(), 20),
                log.getChangeType(),
                truncateString(log.getOldValue(), 20),
                truncateString(log.getNewValue(), 20),
                log.getUserInitiated()));
        }
    }

    public static void viewLogsByType(String changeType) {
        System.out.println("\n=== Logs for " + changeType + " ===");
        inventoryLogs.stream()
            .filter(log -> log.getChangeType().equals(changeType))
            .forEach(System.out::println);
    }

    private static String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
}