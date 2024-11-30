package Inventory;

import java.util.ArrayList;
import java.util.List;

public class Supplier {

	private String supName; // Supplier name
	private List<String> meds; // List of medicines
	private static List<Supplier> suppliers = new ArrayList<>(); // List of all suppliers

	// Constructor
	public Supplier(String supName) {
		this.supName = supName;
		this.meds = new ArrayList<>();
	}

	// Constructor with medicines
	public Supplier(String supName, List<String> meds) {
		this.supName = supName;
		this.meds = new ArrayList<>(meds); // Create a new list for safety
	}

	// Load suppliers from InventoryService
	public static void loadSuppliers(InventoryService inventoryService) {
		if (inventoryService == null) {
			throw new IllegalArgumentException("InventoryService cannot be null");
		}

		for (InventoryItem item : inventoryService.getAllItems()) {
			Supplier existingSupplier = findSupplierByName(item.getSupplier());
			if (existingSupplier == null) {
				Supplier newSupplier = new Supplier(item.getSupplier());
				newSupplier.addMedicine(item.getName());
				suppliers.add(newSupplier);
			} else {
				existingSupplier.addMedicine(item.getName());
			}
		}
	}

	// Find a supplier by name
	private static Supplier findSupplierByName(String name) {
		for (Supplier supplier : suppliers) {
			if (supplier.getSupName().equalsIgnoreCase(name)) {
				return supplier;
			}
		}
		return null;
	}

	// Getter for supplier name
	public String getSupName() {
		return supName;
	}

	// Setter for supplier name
	public void setSupName(String supName) {
		this.supName = supName;
	}

	// Getter for medicines
	public List<String> getMedicines() {
		return meds;
	}

	// Setter for medicines
	public void setMedicines(List<String> meds) {
		this.meds = new ArrayList<>(meds);
	}

	// Add a single medicine
	public void addMedicine(String med) {
		if (!meds.contains(med)) { // Avoid duplicates
			meds.add(med);
		}
	}

	// Get all suppliers
	public static List<Supplier> getSuppliers() {
		return new ArrayList<>(suppliers);
	}

	// String representation of a Supplier
	@Override
	public String toString() {
		return "Supplier{" + "supName='" + supName + '\'' + ", medicines=" + meds + '}';
	}
}
