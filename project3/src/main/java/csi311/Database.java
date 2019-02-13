package csi311;

import java.sql.*;
import java.sql.Driver;

import org.apache.derby.jdbc.EmbeddedDriver;
public class Database {
	private static final String databaseURI = "jdbc:derby:ordersDB;create=true";
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
	
	public Database()
	{
		// Establish the connection.
	}
	
	private void createConnection()
	{
		try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            this.sqlConnection = DriverManager.getConnection(this.databaseURI);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	// We need to create our tables.
	private void createTables()
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			this.sqlStatement.execute("CREATE TABLE Tenants (tenantId INT NOT NULL PRIMARY KEY)");
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			if(!this.tableAlreadyExists(ex))
			{
				System.out.println(ex);
			}
		}
		
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			this.sqlStatement.execute("CREATE TABLE StateMachine (stateMachineId INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), tenantId INT NOT NULL REFERENCES Tenants(tenantId), stateName VARCHAR(30) NOT NULL, transitions VARCHAR(500) NOT NULL)");
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
		
	}
	
	private void dropTable(String tableName)
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			this.sqlStatement.execute("DROP TABLE " + tableName);
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	private boolean tableAlreadyExists(SQLException e) {
        boolean exists;
        if(e.getSQLState().equals("X0Y32")) {
            exists = true;
        } else {
            exists = false;
        }
        return exists;
    }
	
	private void selectTenants()
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			ResultSet results = this.sqlStatement.executeQuery("SELECT * FROM Tenants");
			ResultSetMetaData md = results.getMetaData();
			
			for (int i=1; i <= md.getColumnCount(); i++) {
                //print Column Names
                System.out.print(md.getColumnLabel(1)+"\t\t");  
            }
			System.out.println("\n-------------------------------------------------");
			while(results.next()) {
				int tenantId = results.getInt(1);
				System.out.println(tenantId);
            }
			results.close();
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
	private void selectStateMachine()
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			ResultSet results = this.sqlStatement.executeQuery("SELECT * FROM StateMachine");
			ResultSetMetaData md = results.getMetaData();
			
			for (int i=1; i <= md.getColumnCount(); i++) {
                //print Column Names
                System.out.print(md.getColumnLabel(i)+"\t\t");  
            }
			System.out.println("\n---------------------------------------------------------------------------------------------");
			while(results.next()) {
				int stateMachineId = results.getInt(1);
				int tenantId = results.getInt(2);
				String state = results.getString(3);
				String transitions = results.getString(4);
				System.out.println(stateMachineId + "\t\t\t" + tenantId + "\t\t\t" + state + "\t\t\t" + transitions);
            }
			results.close();
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
	private void insertStateMachine()
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			this.sqlStatement.execute("INSERT INTO StateMachine VALUES (DEFAULT, 12345, 'PENDING', 'Pending, Cancelled, Fulfilled')");
			this.sqlStatement.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
	}
	private void insertTenant(int tenantId)
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			this.sqlStatement.execute("INSERT INTO TENANTS VALUES (" + tenantId + ")");
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
	public static void main(String [] args)
	{
		Database db = new Database();
		db.createConnection();
		db.createTables();
		db.selectTenants();
		db.selectStateMachine();
		
		db.insertTenant(12345);
		db.insertStateMachine();
		
		db.selectStateMachine();
		db.selectTenants();
	}
	
}
