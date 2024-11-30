package Inventory;

import java.util.List;

public class Supplier {
	
	
	private String supName;
	private List<String> meds;
	
	// Constructors
	public Supplier (String supName, List<String> meds) {
		this.supName = supName;
		this.meds = meds;
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
