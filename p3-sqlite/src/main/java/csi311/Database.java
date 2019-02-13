package csi311;

import java.sql.*;

public class Database {
	
	private final String dbConnectionURI = "jdbc:sqlite:ordersdb.db";
	private Connection connection;
	private Statement statement;
	private MachineSpec machineSpec;
	
	public Database(MachineSpec machineSpec)
	{
		try {
			this.connection = DriverManager.getConnection(this.dbConnectionURI);
			System.out.println("Successfully connected to the database.");
			this.machineSpec = machineSpec;
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public MachineSpec getMachineSpec()
	{
		return this.machineSpec;
	}
	public void createSchema()
	{
		// We want to create some tables.
		this.executeStatement("DROP TABLE IF EXISTS Tenants");
		this.executeStatement("DROP TABLE IF EXISTS StateMachine");
		this.executeStatement("Create table IF NOT EXISTS Tenants (tenantId INTEGER NOT NULL PRIMARY KEY)");
		this.executeStatement("Create table IF NOT EXISTS StateMachine " +
				"(stateMachineId INTEGER NOT NULL PRIMARY KEY, " +
				"tenantId INTEGER NOT NULL, " + 
				"FOREIGN KEY (tenantId) REFERENCES Tenants(tenantId))");
		
		this.executeStatement("CREATE TABLE IF NOT EXISTS State (" + 
				"stateId INTEGER NOT NULL PRIMARY KEY, " +
				"stateMachineId INTEGER NOT NULL, " +
				"stateName VARCHAR(255) NOT NULL, "+
				"transitions VARCHAR(500) NOT NULL, "
				+ "FOREIGN KEY (stateMachineId) REFERENCES StateMachine(stateMachineId))");
		
	}
	
	public void displayTenants()
	{
		try {
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
			ResultSet result = this.statement.executeQuery("SELECT * FROM StateMachine");
			ResultSetMetaData md = result.getMetaData();
			
			for (int i = 1; i <= md.getColumnCount(); i++) {
                //print Column Names
                System.out.print(md.getColumnLabel(i)+"\t\t");  
            }
			System.out.println("\n-------------------------------------------------");
			while(result.next()) {
				int stateMachineId = result.getInt(1);
				int tenantId = result.getInt(2);
				System.out.println(stateMachineId + "\t\t\t" + tenantId);
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
				System.out.println(stateId + "\t\t\t" + stateMachineId + "\t\t\t" + stateName + "\t\t\t" + transitionSet);
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
