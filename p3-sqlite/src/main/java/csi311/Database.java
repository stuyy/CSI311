package csi311;

import java.sql.*;

public class Database {
	
	private final String dbConnectionURI = "jdbc:sqlite:ordersdb.db";
	private Connection connection;
	private Statement statement;
	public Database()
	{
		try {
			this.connection = DriverManager.getConnection(this.dbConnectionURI);
			System.out.println("Successfully connected to the database.");
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
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
	}
	
	public void displayTenants()
	{
		try {
			ResultSet result = this.statement.executeQuery("SELECT * FROM Tenants");
			ResultSetMetaData md = result.getMetaData();
			
			for (int i = 1; i <= md.getColumnCount(); i++) {
                //print Column Names
                System.out.print(md.getColumnLabel(1)+"\t\t");  
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
