package Inventory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryApp {

    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService();
        String filePath = "Inventory Data.csv";

        // Read CSV and create/update Inventory items
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true; // Skip the header

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header line
                }

                String[] values = line.split(",");
                if (values.length != 8) { // Adjusted for 8 fields including id
                    System.out.println("Skipping malformed line: " + line);
                    continue; // Skip lines that don't match the expected format
                }

                String name = values[0].trim();
                int id = Integer.parseInt(values[1].trim()); // Parse ID
                double price = Double.parseDouble(values[2].replace("$", "").trim());
                int quantity = Integer.parseInt(values[3].trim());
                int amtSold = Integer.parseInt(values[4].trim());
                String expDate = values[5].trim();
                boolean conSubstance = Boolean.parseBoolean(values[6].trim());
                List<String> allergens = new ArrayList<>(Arrays.asList(values[7].trim().split(",")));

                // Add item to inventory with specified ID
                InventoryItem item = new InventoryItem(id, name, quantity, price, amtSold, expDate, allergens, conSubstance);
                inventoryService.addItem(name, quantity, price, amtSold, expDate, allergens, conSubstance);
            }

            System.out.println("Inventory loaded successfully!");
            inventoryService.getAllItems().forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
