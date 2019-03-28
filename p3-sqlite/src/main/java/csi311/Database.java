package csi311;

import java.sql.*;
import java.util.List;
import java.util.Map;

import csi311.MachineSpec.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class Database implements OrderParser {
	
	private final String dbConnectionURI = "jdbc:sqlite:orders.db";
	private Connection connection;
	private Statement statement;
	private MachineSpec machineSpec;
	private HashMap<String, List> cachedStates;

	private ArrayList<String> invalidOrders;
	private HashMap<String, Order> orders;
	private ArrayList<String> startStates;
	private ArrayList<String> terminalStates;
	private HashMap<String, Integer> correctOrderCount;
	public Database()
	{

		this.cachedStates = new HashMap<String, List>();
		this.terminalStates = new ArrayList<String>();
		this.orders = new HashMap<String, Order>();
		this.startStates = new ArrayList<String>();
		this.invalidOrders = new ArrayList<String>();
		this.correctOrderCount = new HashMap<String, Integer>();
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection(this.dbConnectionURI);
			System.out.println("Success.");
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
	}
	
	public boolean tenantExists(int tenantId)
	{
		try {
			this.statement = this.connection.createStatement();
			ResultSet result = this.statement.executeQuery("SELECT * FROM StateMachine WHERE stateMachineTenantId = " + tenantId);
			if(result.next())
				return true;
			else {
				return false;
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			return false;
		}
	}
	public void insertStateMachine(MachineSpec ms, String json)
	{
		this.insertTenant(ms.getTentantId()); // First insert the tenant.
		this.executeStatement("INSERT INTO StateMachine VALUES(" + ms.getTentantId() + ", " + "'" + json + "'" + ")");
	}
	public MachineSpec getMachineSpec()
	{
		return this.machineSpec;
	}
	
	public void insertTenant(int id)
	{
		this.executeStatement("INSERT INTO Tenants VALUES(" + id + ")");
	}
	
	/**
	 *  Since we only need to make the tables once, this method will only be called on the --state argument. So we can delete the tables in the beginning.
	 *  
	 */
	public void createSchema()
	{
		this.executeStatement("Create table IF NOT EXISTS Tenants (tenantId INTEGER NOT NULL PRIMARY KEY)");
		
		this.executeStatement("CREATE TABLE IF NOT EXISTS StateMachine (stateMachineTenantId INTEGER NOT NULL PRIMARY KEY, machineSpec VARCHAR(10000) NOT NULL)");
		this.executeStatement("CREATE TABLE IF NOT EXISTS Orders (" +
				"tenantID INTEGER NOT NULL, " +
				"FOREIGN KEY (tenantID) REFERENCES Tenants(tenantId))"
				);
		
		this.executeStatement("CREATE TABLE IF NOT EXISTS OrdersFromFile (orderTenantId INTEGER NOT NULL, OrderString VARCHAR(10000) NOT NULL, FOREIGN KEY (orderTenantId) REFERENCES StateMachine(stateMachineTenantId))");
	}
	
	public void setMachineSpec(int tenantId) 
	{
		try {
			this.statement = this.connection.createStatement();
			ResultSet result = this.statement.executeQuery("SELECT * FROM StateMachine WHERE stateMachineTenantId = " + tenantId);
			
			String curr = result.getString(2);
			this.machineSpec = FileProcessor.parseJson(curr);
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	public void processReport(int tenantId)
	{
		
		try {
			this.setMachineSpec(tenantId);
			
			this.setTerminalStates();
			this.setStartStates();
			
			this.statement = this.connection.createStatement();
			ResultSet result = this.statement.executeQuery("SELECT * FROM OrdersFromFile WHERE orderTenantId = " + tenantId);
			ResultSetMetaData md = result.getMetaData();
			
			while(result.next()) {
				
				String curr = result.getString(2);
				String line = "";
		    	String [] tokens;
		    	tokens = curr.split(",");
		    	
		    	this.trimWhitespace(tokens);
		    	this.validateFields(tokens);
		    	
		    	boolean status = this.validateFields(tokens);
		    	
		    	if(status)	
		    	{
		    		// Inside here, we will first check if the HashMap contains the OrderID as a Key.
		    		// First we should check the invalid orders, if the order id is in the invalid order list, then it cannot be deemed as valid.
		    		if(this.invalidOrders.contains(tokens[2]))
		    			continue;
		    		
		    		if(this.orders.containsKey(tokens[2]))
		    		{
		    			// We see that the order does contain the current key.
		    			String currentState = this.orders.get(tokens[2]).getState(); // Get the current state of the order.
		    			// We already validated everything, so whichever state was retrieved from the Order object returned from the key is a valid state.
		    			String nextState = tokens[4];
		    			// Let's call a method passing in the current state and the next state and see if it's a valid transition.
		    			boolean validTransition = this.isValidTransition(currentState, nextState);
		    			// We need to now update the state if it's a valid transition.
		    			if(validTransition) // Update state.
		    				this.updateOrderState(tokens[2], nextState);
		    			
		    			else { // If it's not a valid transition, we will remove the already validated order from the HashMap and add it to the list of invalid orders.
		    				
		    				System.out.println("Flagging order: " + tokens[2]);
		    				if(this.orders.containsKey(tokens[2]))
		    					this.orders.remove(tokens[2]);
		    				
		    				if(this.invalidOrders.contains(tokens[2]))
		    					continue;
		    				else
		    					this.invalidOrders.add(tokens[2]);
		    			}
		    		}
		    		else { // If the orderID does not exist as a key in the HashMap, we will create a new Order
		    			// Object and add it as a value of the orderID key.
		    			Order order = new Order(tokens[1], tokens[2], tokens[3], 
		        				tokens[4], tokens[5], Integer.parseInt(tokens[6]), Float.parseFloat(tokens[7]));
		    			
		        		// Since the order is valid, let's add it to a Map, where the key will be the orderID and it's value will be an Order.
		        		this.orders.put(order.getOrderID(), order);
		    		}
		    	}
		    	else // We can immediately add the invalid orders (the ones with invalid fields) to a List.
		    	{
		    		System.out.println("Flagging order: " + tokens[2]);
		    		if(this.invalidOrders.contains(tokens[2]))
						continue;
					else
						this.invalidOrders.add(tokens[2]);
		    	}
		    	
            }
			result.close();
			this.statement.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
		finally {
			for(String i : this.invalidOrders)
				System.out.println(i);
			
			this.generateReport();
		}
		
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
    	boolean [] validFields = new boolean[7];
    	if(fields.length != 8)
    		return false;
    	else {
    		
    		if(this.terminalStates.contains(fields[4].toLowerCase()))
    			validFields[4] = true;
    		else
    			validFields[4] = this.isValidState(fields[4]);


    		validFields[0] = this.isValidTenantId(Integer.parseInt(fields[0]));
    		validFields[1] = this.isValidTimestamp(fields[1]);
    		validFields[2] = this.isValidOrderID(fields[2]);
    		validFields[3] = this.isValidCustomerID(fields[3]);
    		validFields[5] = this.isValidQuantity(fields[6]);
    		validFields[6] = this.isValidPrice(fields[7]);
    		
    		// We only need to check if the state is a start state if the order is not in the HashMap.
    		
    		if(this.orders.containsKey(fields[2]))
    		{
    			
    		}
    		else {	
    			// Check if the state that was validated is a start state.
    			
    			if(this.isValidStartState(fields[4]))
    			{
    				validFields[4] = true;
    			}
    			else
    				validFields[4] = false;
    		}
    		
    		return validFields[0] && validFields[1] && validFields[2] && validFields[3] && validFields[4] && validFields[5] && validFields[6];
    	}
    }
	
	private boolean isValidStartState(String state)
    {
    	if(this.startStates.contains(state.toLowerCase()))
    	{
    		return true;
    	}
    	else
    		return false;
    }
	/*
	public void dropSchema()
	{
		this.executeStatement("DROP TABLE Tenants");
		this.executeStatement("DROP TABLE StateMachine");
		this.executeStatement("DROP TABLE State");
		this.executeStatement("DROP TABLE MachineSpec");
	}*/
	
	public void displayTenants()
	{
		
		try {
			this.statement = this.connection.createStatement();
			ResultSet result = this.statement.executeQuery("SELECT * FROM Tenants");
			ResultSetMetaData md = result.getMetaData();
			
			
			for (int i = 1; i <= md.getColumnCount(); i++) {
                //print Column Names
				
                System.out.print(md.getColumnLabel(i)+"\t\t");  
            }
			System.out.println("\n-------------------------------------------------");
			while(result.next()) {
				int tenantId = result.getInt(1);
				System.out.println(tenantId);
            }
			result.close();
			this.statement.close();
			
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
	public void displayStateMachines()
	{
		try {
			this.statement = this.connection.createStatement();
			ResultSet result = this.statement.executeQuery("SELECT * FROM StateMachine");
			ResultSetMetaData md = result.getMetaData();
			
			for (int i = 1; i <= md.getColumnCount(); i++) {
                //print Column Names
                System.out.print(md.getColumnLabel(i)+"\t\t");  
            }
			System.out.println("\n-------------------------------------------------");
			while(result.next()) {
				int stateMachineId = result.getInt(1);
				String msString = result.getString(2);
				System.out.println(stateMachineId + "\t\t\t\t" + msString);
            }
			result.close();
			this.statement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
	public void displayStates()
	{
		try {
			this.statement = this.connection.createStatement();
			ResultSet result = this.statement.executeQuery("SELECT * FROM State");
			ResultSetMetaData md = result.getMetaData();
			
			for (int i = 1; i <= md.getColumnCount(); i++) {
                //print Column Names
                System.out.print(md.getColumnLabel(i)+"\t\t");  
            }
			System.out.println("\n----------------------------------------------------------------------------");
			while(result.next()) {
				int stateId = result.getInt(1);
				int stateMachineId = result.getInt(2);
				String stateName = result.getString(3);
				String transitionSet = result.getString(4);
				System.out.printf("%1s\t%20s\t%20s\t\t%12s\n", stateId, stateMachineId, stateName, transitionSet);
            }
			result.close();
			this.statement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	public void executeStatement(String sqlQuery)
	{
		try {
			this.statement = this.connection.createStatement();
			this.statement.execute(sqlQuery); // Create the table only if it doesn't exist.
			this.statement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
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
	
	public boolean isValidTenantId(int tenantId)
	{
		return this.machineSpec.getTentantId() == tenantId;
	}
	
	private void setStartStates()
	{

		for(State st : this.machineSpec.getMachineSpec())
		{
			if(st.getState().equalsIgnoreCase("start"))
			{
				for(String s : st.getTransitions())
					this.startStates.add(s);
			}
		}
		
	}
	private void setTerminalStates()
	{
		// Loop through the states, check the transitions. If the transitions match any of the states, continue.
		// If they don't, then the transition is a terminal state, add it to the list of terminal states.
		for(State st : this.machineSpec.getMachineSpec())
		{
			if(st.getState().equalsIgnoreCase("start")) // Ignore Start State
				continue;
			
			List<String> transitions = st.getTransitions();
			for(String t : transitions)
			{
				if(this.isValidState(t))
					continue;
				else {
					if(this.terminalStates.contains(t))
						continue;
					else
						this.terminalStates.add(t);
				}
			}
			
		}
	}
}
