package csi311;

import csi311.MachineSpec.StateTransitions; 

public class Order {
	private String timestamp;
	private String orderID;
	private String customerID;
	private String state;
	private String orderDescription;
	private int quantity;
	private float price;
	
	public Order()
	{
		
	}
	public Order(String timestamp, String orderID, 
			String customerID, 
			String state, 
			String orderDescription,
			int quantity, float price)
	{
		
		this.timestamp = timestamp;
		this.orderDescription = orderDescription;
		this.orderID = orderID;
		this.customerID = customerID;
		this.quantity = quantity;
		this.price = price;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOrderDescription() {
		return orderDescription;
	}
	public void setOrderDescription(String orderDescription) {
		this.orderDescription = orderDescription;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	@Override
	public String toString() {
		return "Order [timestamp=" + timestamp + ", orderID=" + orderID + ", customerID=" + customerID + ", state="
				+ state + ", orderDescription=" + orderDescription + ", quantity=" + quantity + ", price=" + price
				+ "]";
	}
}	
