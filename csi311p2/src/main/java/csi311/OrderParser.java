package csi311;

public interface OrderParser {
	
	public boolean isValidTimestamp(String timestamp);
	public boolean isValidOrderID(String orderID);
	public boolean isValidCustomerID(String customerID);
	public boolean isValidState(String state);
	public boolean isValidOrderDescription(String orderDescription);
	public boolean isValidQuantity(int quantity);
	public boolean isValidPrice(float price);
	
}
