package csi311;
/**
 * An interface to parse orders. Each method returns a boolean value. An order that is considered to
 * be flagged will either have:
 * - An invalid timestamp
 * - An invalid quantity
 * - An invalid customerID
 * - An invalid cost
 * - An invalid state
 * @author Anson
 *
 */
public interface OrderParser {
	
	public boolean isValidTimestamp(String timestamp);
	public boolean isValidOrderID(String orderID);
	public boolean isValidCustomerID(String customerID);
	public boolean isValidState(String state);
	//public boolean isValidOrderDescription(String orderDescription);
	public boolean isValidQuantity(int quantity);
	public boolean isValidPrice(float price);
	
}
