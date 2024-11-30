****Inventory Management System**

**Description**

This is a pharmacy inventory management system implemented in Java. It allows users to manage inventory items, update supplier information, check low stock, process reorders, and display inventory details. The system reads inventory data from a CSV file (Inventory.txt) and provides a command-line interface for user interaction.

**Features**

Load inventory data from a CSV file.
Display the full inventory list with detailed information.
Update supplier details, inventory items, stock levels, and expiry dates.
Check for low-stock items and automatically generate reorder suggestions.
Calculate the total value of inventory items.
Manage allergens, controlled substances, and supplier information.
Files and Classes

**1. Main.java**
Entry point of the application.
Provides a command-line interface with the following commands:
update supplier: Update supplier details for an inventory item.
update inventory: Update details like price or expiry date.
update stock: Add or reduce stock quantities.
update expired: Update the expiry date of an item.
check low stock: Identify items below the low stock threshold.
reorder: Process reorders for low-stock items.
print inventory: Display the complete inventory.
exit: Exit the application.
Delegates inventory loading to InventoryApp.loadInventoryFromFile.
**2. InventoryApp.java**
Handles the loading of inventory data from a CSV file (Inventory.txt).
Uses the InventoryService class to create or update inventory items.
Skips malformed or invalid lines in the CSV file.
Implements the method:
loadInventoryFromFile(InventoryService inventoryService, String fileName): Reads inventory data and populates the inventory.
**3. InventoryService.java**
Manages the inventory list.
Provides methods for:
Adding items (with or without IDs).
Retrieving all items.
Updating or deleting items by ID.
Tracks the next available ID for new inventory items.
**4. InventoryItem.java**
Represents an individual inventory item.
Includes fields for:
ID, name, quantity, price, amount sold, expiry date, supplier, controlled substance status, and allergens.
Provides methods for:
Updating quantity.
Calculating the total value of the stock.
Includes a toString() method for formatted item details.
Setup and Usage

**Requirements**
Java Development Kit (JDK) 8 or higher.
**A text file named Inventory.txt containing inventory data in the following CSV format:**
ID,Name,Quantity,Price,AmountSold,ExpiryDate,ControlledSubstance,Supplier,Allergens
1,Paracetamol,100,$5.50,30,2024-01-01,false,Pharma Inc.,None
2,Ibuprofen,50,$7.00,20,2024-06-01,true,Health Supplies,Gluten;Lactose


update supplier
print inventory
check low stock
Design Highlights

**Separation of Concerns: Each class has a distinct role:**
**Main.java** handles the interface.
**InventoryApp.java handles** file I/O.
**InventoryService.java** manages inventory operations.
**InventoryItem.java** represents individual items.
**Flexibility:** Easily add new commands or features by extending the actionTask method in Main.java.
**Error Handling:** Malformed CSV lines are skipped with appropriate error messages.
