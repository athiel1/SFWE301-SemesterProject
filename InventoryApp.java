package Inventory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class InventoryApp {
	public static void loadInventoryFromFile(InventoryService inventoryService, String fileName) {
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(
	            InventoryApp.class.getResourceAsStream("/Inventory/" + fileName)))) {
	        String line;
	

	        while ((line = br.readLine()) != null) {
	         

	            String[] values = line.split(",");
	            if (values.length != 9) {
	                System.out.println("Skipping malformed line: " + line);
	                continue;
	            }

	            try {
	                int id = Integer.parseInt(values[0].trim());
	                String name = values[1].trim();
	                int quantity = Integer.parseInt(values[2].trim());
	                double price = Double.parseDouble(values[3].replace("$", "").trim());
	                int amountSold = Integer.parseInt(values[4].trim());
	                String expDate = values[5].trim();
	                boolean conSubstance = Boolean.parseBoolean(values[6].trim());
	                String supplier = values[7].trim();
	                List<String> allergens = new ArrayList<>(Arrays.asList(values[8].trim().split(";")));

	                inventoryService.addItem(id, name, quantity, price, amountSold, expDate, conSubstance, supplier, allergens);
	            } catch (NumberFormatException e) {
	                System.out.println("Error parsing values in line: " + line);
	                continue;
	            }
	        }
	        System.out.println("Inventory loaded successfully!");
	    } catch (IOException | NullPointerException e) {
	        System.out.println("Error reading file: " + fileName);
	        e.printStackTrace();
	    }
	}

}
