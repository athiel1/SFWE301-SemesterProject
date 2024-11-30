package Inventory;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
	
	private static InventoryService inventoryService;
	
	private String supName;
	private List<String> meds;
	private String med;
	
	private List<Supplier> suppliers = new ArrayList<>();
	
	private int foundSupplier = 0;
	
	// Load suppliers list
	for (InventoryItem item : inventoryService.getAllItems()) {
		for (Supplier sup : suppliers) {
			if (item.getSupplier() == sup.getSupName()) {
				foundSupplier = 1;
			}
		}
		if (foundSupplier == 0) {
			Supplier newSup = new Supplier(item.getSupplier(), item.getName());
			suppliers.add(newSup);
		}
		foundSupplier = 0;
	};
	
	// Constructors
	public Supplier (String supName, List<String> meds) {
		this.supName = supName;
		this.meds = meds;
	}
	
	public Supplier (String supName, String med) {
		this.supName = supName;
		((Supplier) this.meds).addMedicine(med);
	}
	
	// Getter for supplier name
	public String getSupName() {
		return supName;
	}
	
	// Setter for supplier name
	public void setSupName(String supName) {
		this.supName = supName;
	}
	
	// Getter for medicines with a supplier
	public List<String> getMedicines() {
		return meds;
	}
	
	// Setter for adding a list of medicines to a supplier
	public void setMedicines(List<String> meds) {
		this.meds = meds;
	}
	
	// Setter for adding a medicine to a supplier
	public void addMedicine(String med) {
		meds.add(med);
	}
	
		
	

}
