package Inventory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

/**
 * Class representing a single log entry in the inventory system.
 * Contains details about changes made to inventory items including
 * timestamps, item details, and the nature of changes made.
 */
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
    public int getLogId() { return logId; }
}

/**
 * Manages the logging system for the inventory management application.
 * Provides functionality to:
 * - Log inventory changes
 * - Write logs to a file
 * - View logs by different criteria
 * - Maintain an in-memory log history
 * 
 * The logs are both stored in memory and written to a physical file
 * for persistence across program executions.
 */
public class LogManager {
    private static List<InventoryLog> inventoryLogs = new ArrayList<>();
    private static int nextLogId = 1;
    private static final String LOG_FILE = "src/Inventory/log.txt";
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a change in the inventory system.
     * Records the change both in memory and in the log file.
     * 
     * @param item The inventory item that was changed
     * @param changeType The type of change made
     * @param oldValue The previous value
     * @param newValue The new value
     * @param user The user who made the change
     */
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

    /**
     * Writes a log entry to the log file.
     * Creates the file if it doesn't exist.
     * Appends the log entry to the existing file if it exists.
     * 
     * @param log The log entry to write to the file
     */
    private static void writeToFile(InventoryLog log) {
        // Create the logs directory if it doesn't exist
        File logFile = new File(LOG_FILE);
        logFile.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(logFile, true);
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
            
            // Ensure the log is written immediately
            out.flush();
            
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays all logged changes in a formatted table view.
     * Shows detailed information about each change including timestamps,
     * item details, and the nature of changes.
     */
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

    /**
     * Displays logs filtered by a specific change type.
     * 
     * @param changeType The type of change to filter by (e.g., "REORDER", "EXPIRY_REMOVAL")
     */
    public static void viewLogsByType(String changeType) {
        System.out.println("\n=== Logs for " + changeType + " ===");
        inventoryLogs.stream()
            .filter(log -> log.getChangeType().equals(changeType))
            .forEach(System.out::println);
    }

    /**
     * Truncates a string to a specified length, adding "..." if truncated.
     * 
     * @param str The string to truncate
     * @param length The maximum length of the resulting string
     * @return The truncated string
     */
    private static String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
}
