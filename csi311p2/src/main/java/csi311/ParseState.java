/*
 * 	1543113695001, 001-ABC-4567, 123456789, PENDING  ,   bagel toaster             ,   1,    19.95
	1543113695002, 001-ABC-4567, 123456789, submitted,   bagel toaster             ,   2,    39.90
	1543113695003, 001-ABC-4567, 123456789, fulfilled,   bagel toaster             ,   2,    39.90
	1543113695004, 002-123-4567, 123456789, fulfilled,   bagel                     ,   1,     1.50
	1543113695005, 003-AAA-4599, 123456799, PENDING  ,   bagel toaster             ,   1,    19.95
	1543113695006, 003-AAA-4599, 123456799, cancelled,   bagel toaster             ,   1,    19.95
	1543113695007, 003-AAA-4599, 123456799, fulfilled,   bagel toaster             ,   1,    19.95
	1543113695008, 004-AAA-4598, 123456799, pending,     bagel toaster             ,   1,    19.95
	1543113695009, 005-AAA-4597, 123456799, pending,     bagel toaster             ,   1,    19.95
	1543113695010, 005-AAA-4597, 123456799, cancelled,   bagel toaster             ,   1,    19.95
	1543113695011, 006-AAA-4597, abcdefghi, pending,     bagel toaster             ,   1,    19.95
	1543113695011, 006-AAA-4597, abcdefghi, pending,     bagel toaster             ,   1,    19.95
	1543113695012, 007-AAA-4597, 112233445, pending,     bagel toaster             ,   1,    19.95
	1543113695013, 007-AAA-4597, 112233445, submitted,   bagel toaster             ,   1,    19.95
	1543113695014, 007-AAA-4597, 112233445, backordered, bagel toaster             ,   1,    19.95
	1543113695015, 007-AAA-4597, 112233445, fulfilled,   bagel toaster             ,   1,    19.95
 */

package csi311;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import csi311.MachineSpec.State; 

public class ParseState implements OrderParser {
	
	private MachineSpec machineSpec;
	private HashMap<String, Order> orders;
	private ArrayList<String> invalidOrders;
	private HashMap<String, Integer> correctOrderCount;
	// We need the flagged orders.
	// We need all orders that are pending.
	// We need all orders that are fulfilled.
	// We need all orders that are cancelled.

	public ParseState(String machineFileDesc, String orderFileDesc) throws Exception
	{
		
		if(!(new File(machineFileDesc).exists()) || !(new File(orderFileDesc).exists()))
			throw new Exception("File/s do not exist.");
		else {
			
			// Process the JSON file.
			String machineJSONString = processFile(machineFileDesc);
			
			this.machineSpec = parseJson(machineJSONString);
			this.orders = new HashMap<String, Order>();
			this.invalidOrders = new ArrayList<String>();
			this.correctOrderCount = new HashMap<String, Integer>();
			this.processOrders(orderFileDesc);
		}
	}
    /**
     * Takes in a file containing the orders and parses everything line by line.
     * @param orders
     * @throws IOException
     */
    private void processOrders(String orders) throws IOException
    {
    	// Read in the file containing the orders.
    	// We can assume the commas will appear in the correct position. 
    	
    	FileReader file = new FileReader(orders);
    	BufferedReader reader = new BufferedReader(file);
    	
    	String line = "";
    	String [] tokens;
    	while((line = reader.readLine()) != null)
    	{
        	tokens = line.split(","); // Split the line into tokens, delimiting it with a comma.
    		// For each iteration, we want to validate the tokens.
    		// But we need to also check if the OrderID has already been appended to the HashMap.
    		
    		// First we shall validate each field.
        	this.trimWhitespace(tokens); // Trim any whitespace.
        	boolean status = this.validateFields(tokens);
        	// If true, then we have a VALID LINE. However, we still need to validate the transition.
        	// First, let's add the Order ID to a HashMap.
        	if(status)
        	{
        		// Inside here, we will first check if the HashMap contains the OrderID as a Key.
        		
        		if(this.orders.containsKey(tokens[1]))
        		{
        			// We see that the order does contain the current key.
        			String currentState = this.orders.get(tokens[1]).getState(); // Get the current state of the order.
        			// We already validated everything, so whichever state was retrieved from the Order object returned from the key is a valid state.
        			String nextState = tokens[3];
        			// Let's call a method passing in the current state and the next state and see if it's a valid transition.
        			boolean validTransition = this.isValidTransition(currentState, nextState);
        			// We need to now update the state if it's a valid transition.
        			if(validTransition) // Update state.
        				this.updateOrderState(tokens[1], nextState);
        			
        			else { // If it's not a valid transition, we will remove the already validated order from the HashMap and add it to the list of invalid orders.
        				
        				System.out.println("Flagging order: " + tokens[1]);
        				if(this.orders.containsKey(tokens[1]))
        					this.orders.remove(tokens[1]);
        				
        				if(this.invalidOrders.contains(tokens[1]))
        					continue;
        				else
        					this.invalidOrders.add(tokens[1]);
        			}
        		}
        		else { // If the orderID does not exist as a key in the HashMap, we will create a new Order
        			// Object and add it as a value of the orderID key.
        			Order order = new Order(tokens[0], tokens[1], tokens[2], 
            				tokens[3], tokens[4], Integer.parseInt(tokens[5]), Float.parseFloat(tokens[6]));
            		// Since the order is valid, let's add it to a Map, where the key will be the orderID and it's value will be an Order.
            		this.orders.put(order.getOrderID(), order);
        		}
        	}
        	else // We can immediately add the invalid orders (the ones with invalid fields) to a List.
        	{
        		System.out.println("Flagging order: " + tokens[1]);
        		if(this.invalidOrders.contains(tokens[1]))
					continue;
				else
					this.invalidOrders.add(tokens[1]);
        	}
        	
        	
    	}
    	// In the end, we should have a Mapping of all Order ID's to it's Order.
    	this.generateReport();
    	
    }
    
    private void generateReport()
    {
    	// Print the invalid orders first.
    	
    	Iterator iter = this.orders.entrySet().iterator();
    	while(iter.hasNext())
    	{
    		Map.Entry pair = (Map.Entry) iter.next();
    		String key = (String) pair.getKey();
    		Order order = this.orders.get(key);
    		if(this.correctOrderCount.containsKey(order.getState()))
    		{
    			int newValue = this.correctOrderCount.get(order.getState());
    			this.correctOrderCount.put(order.getState(), ++newValue);
    		}
    		else {
    			this.correctOrderCount.put(order.getState(), 1);
    		}
    	}
    	
    	iter = this.correctOrderCount.entrySet().iterator();
    	while(iter.hasNext())
    	{
    		Map.Entry pair = (Map.Entry) iter.next();
    		System.out.println(pair.getKey() + ": " + pair.getValue());
    	}
    	System.out.println("flagged: " + this.invalidOrders.size());
    		
    }
    /**
     * Takes in a state and a transition to another state and validates if that state transition is legal.
     * @param currentState
     * @param nextState
     * @return
     */
    private boolean isValidTransition(String currentState, String nextState)
    {
    	if(!this.isValidState(currentState) && !this.isValidState(nextState))
    		return false;
    	
    	else
    	{
    		List<State> states = this.machineSpec.getMachineSpec();
        	for(State t : states)
        		if(t.getState().equalsIgnoreCase(currentState))
            		if(t.getTransitions().contains(nextState))
            			return true; // If the transition is part of the State, we return true.
        	
    	}
    	return false;
    }
    
    private void updateOrderState(String key, String nextState)
    {
    	if(this.orders.containsKey(key))
    	{
    		this.orders.get(key).setState(nextState);
    	}
    }
    private void trimWhitespace(String [] tokens)
    {
    	for(int i = 0; i < tokens.length; i++)
    		tokens[i] = tokens[i].replaceAll("\\s", "");
    }
    
    private boolean validateFields(String [] fields)
    {
    	boolean [] validFields = new boolean[6];
    	if(fields.length != 7)
    		return false;
    	else {
    		
    		if(fields[3].equalsIgnoreCase("fulfilled") || fields[3].equalsIgnoreCase("cancelled"))
    			validFields[3] = true;
    		else
    			validFields[3] = this.isValidState(fields[3]);
    		
    		validFields[0] = this.isValidTimestamp(fields[0]);
    		validFields[1] = this.isValidOrderID(fields[1]);
    		validFields[2] = this.isValidCustomerID(fields[2]);
    		validFields[4] = this.isValidQuantity(fields[5]);
    		validFields[5] = this.isValidPrice(fields[6]);
    		
    		return validFields[0] && validFields[1] && validFields[2] && validFields[3] && validFields[4] && validFields[5];
    	}
    }
    
    
    /**
     * Method provided from the sample code 1.5
     * Takes in a MachineSpec instance and prints out all of the States and Transitions for each State.
     * @param machineSpec
     */
    private void dumpMachine(MachineSpec machineSpec) {
    	if (machineSpec == null) {
    		return;
    	}
    	for (State st : machineSpec.getMachineSpec()) {
    		System.out.println(st.getState() + " : " + st.getTransitions());
    	}
    }
    
    /**
     * Method provided from the sample code 1.5
     * @param filename - the file containing the MachineSpec JSON
     * @return JSON object of the MachineSpec
     * @throws Exception
     */
    private String processFile(String filename) throws Exception {
    	System.out.println("Processing file: " + filename); 
    	BufferedReader br = new BufferedReader(new FileReader(filename));  
    	String json = "";
    	String line; 
    	while ((line = br.readLine()) != null) {
    		json += " " + line; 
    	} 
    	br.close();
    	// Get rid of special characters - newlines, tabs.  
    	return json.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " "); 
    }
    /**
     * Method provided from the sample code 1.5
     * @param json - a Stringified version of the JSON objects.
     * @return a MachineSpec instance or null.
     */
    private MachineSpec parseJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try { 
        	MachineSpec machineSpec = mapper.readValue(json, MachineSpec.class);
        	return machineSpec; 
        }
        catch (Exception e) {
            e.printStackTrace(); 
        }
        return null;  	
    }
    
    // ------- BELOW ARE ALL METHODS OVERRIDDEN FROM THE OrderParser INTERFACE! ------- //
    
	public boolean isValidTimestamp(String timestamp) {
		return timestamp.matches("[0-9]{13}");
	}

	public boolean isValidOrderID(String orderID) {
		return orderID.matches("[0-9]{3}\\-[a-zA-z]{3}\\-[0-9]{4}");
	}
	
	public boolean isValidCustomerID(String customerID) {
		return customerID.matches("[0-9]{9}");
	}
	
	/**
	 * Takes in a String and checks if it is a valid state from the List of StateTransitions.
	 * @param state - represents a state
	 * @return boolean - True if the state exists, false if it does not.
	 */
	public boolean isValidState(String state) {
		// We must check the Machine and see if it has the state that was passed in.
		List<State> states = this.machineSpec.getMachineSpec();
		
		for(State t : states)
			if(t.getState().equalsIgnoreCase(state.trim()))
				return true;
		
		return false;
	}
	/**
	 * Check to see if the quantity is a positive integer.
	 * Returns false if the number is negative, or if a NumberMismatchException occurs.
	 * @param quantity a quantity passed in as a String
	 */
	public boolean isValidQuantity(String quantity) {
		try {
			int number = Integer.parseInt(quantity);
			return number > 0;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return false;
		}
	}

	/**
	 * Checks to see if the price passed in is a valid float/numeric type.
	 * @param price - a price of an item
	 * @return boolean - whether is not the price is valid.
	 */
	public boolean isValidPrice(String price) {
		// TODO Auto-generated method stub
		try {
			float number = Float.parseFloat(price);
			return number >= 0.0;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return false;
		}
	}
}
