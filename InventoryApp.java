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
                if (values.length != 9) { // Adjusted for 9 fields
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }

                try {
                    // Parse values in new order
                    int id = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    int quantity = Integer.parseInt(values[2].trim());
                    double price = Double.parseDouble(values[3].replace("$", "").trim());
                    int amountSold = Integer.parseInt(values[4].trim());
                    String expDate = values[5].trim();
                    boolean conSubstance = Boolean.parseBoolean(values[6].trim());
                    String supplier = values[7].trim();
                    List<String> allergens = new ArrayList<>(Arrays.asList(values[8].trim().split(";")));

                    // Add item to inventory
                    inventoryService.addItem(id, name, quantity, price, amountSold, 
                                          expDate, conSubstance, supplier, allergens);

                } catch (NumberFormatException e) {
                    System.out.println("Error parsing values in line: " + line);
                    System.out.println("Error: " + e.getMessage());
                    continue;
                }
            }

            System.out.println("Inventory loaded successfully!");
            inventoryService.getAllItems().forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
