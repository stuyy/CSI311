package csi311;

import java.sql.*;
import java.util.List;
import java.util.Map;

import csi311.MachineSpec.State; 
import java.util.HashMap;
import java.util.Iterator;
public class Database {
	
	private final String dbConnectionURI = "jdbc:sqlite:orders.db";
	private Connection connection;
	private Statement statement;
	private MachineSpec machineSpec;
	private HashMap<String, List> cachedStates;
	
	public Database(MachineSpec machineSpec)
	{
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection(this.dbConnectionURI);
			System.out.println("Successfully connected to the database.");
			this.machineSpec = machineSpec;
			this.cachedStates = new HashMap();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public Database()
	{
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection(this.dbConnectionURI);
			System.out.println("Successfully connected to the database.");
			this.cachedStates = new HashMap();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public void setMachineSpec(MachineSpec machineSpec)
	{
		this.machineSpec = machineSpec;
	}
	
	public MachineSpec getMachineSpec()
	{
		return this.machineSpec;
	}
	
	public void insert()
	{
		// Let's insert the tenantId first.
		this.insertTenant(this.machineSpec.getTentantId());
		System.out.println(this.machineSpec.getTentantId());
		// Now that the tenant is inserted, we will insert the state machine into the db.
		int value = this.insertStateMachine(this.machineSpec.getTentantId());
		System.out.println("The state machine is: " + value);
		// Now insert all of the States with the foreign key value.
		this.insertStates(value);
	}
	// Insert into the tables.
	
	public void insertTenant(int id)
	{
		this.executeStatement("INSERT INTO Tenants VALUES(" + id + ")");
	}
	
	public int insertStateMachine(int tenantId)
	{
		this.executeStatement("INSERT INTO StateMachine (stateMachineTenantId) VALUES (" + tenantId + ")");
		try {
			ResultSet result = this.statement.executeQuery("SELECT * FROM StateMachine WHERE stateMachineTenantId = " + tenantId);
			ResultSetMetaData md = result.getMetaData();
			int stateMachineId = result.getInt(1);
			result.close();
			return stateMachineId;
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
			return -1;
		}
	}
	
	public void insertStates(int stateMachineId)
	{
		List<State> states = this.machineSpec.getMachineSpec();
		for(State s : states)
		{
			List<String> transitions = s.getTransitions();
			this.cachedStates.put(s.getState(), transitions);
		}
		
		Iterator iter = this.cachedStates.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry pair = (Map.Entry) iter.next();
    		
    		String stateName = pair.getKey().toString();
    		String transitionSet = pair.getValue().toString().replaceAll("[\\[\\]]", "").trim();
    		System.out.println(transitionSet);
    		this.executeStatement("INSERT INTO State (stateMachineId, stateName, transitions) VALUES(\'" +  stateMachineId + "\', \'" + stateName + "\', \'" + transitionSet +"\')");
		}
	}
	/**
	 *  Since we only need to make the tables once, this method will only be called on the --state argument. So we can delete the tables in the beginning.
	 *  
	 */
	public void createSchema()
	{
		this.executeStatement("Create table IF NOT EXISTS Tenants (tenantId INTEGER NOT NULL PRIMARY KEY)");
		this.executeStatement("Create table IF NOT EXISTS StateMachine " +
				"(stateMachineTenantId INTEGER NOT NULL PRIMARY KEY, " +
				"FOREIGN KEY (stateMachineTenantId) REFERENCES State(stateMachineId))");
		
		this.executeStatement("CREATE TABLE IF NOT EXISTS State (" + 
				"stateId INTEGER NOT NULL PRIMARY KEY, " +
				"stateMachineId INTEGER NOT NULL, " +
				"stateName VARCHAR(255) NOT NULL, "+
				"transitions VARCHAR(500) NOT NULL, "
				+ "FOREIGN KEY (stateMachineId) REFERENCES StateMachine(stateMachineId))");
	}
	
	public void dropSchema()
	{
		this.executeStatement("DROP TABLE Tenants");
		this.executeStatement("DROP TABLE StateMachine");
		this.executeStatement("DROP TABLE State");
	}
	
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
				System.out.println(stateMachineId);
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
}
