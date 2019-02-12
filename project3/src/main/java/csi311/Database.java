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
			this.sqlStatement.execute("CREATE TABLE IF NOT EXISTS Tenants (" +
					"tenantID INT NOT NULL PRIMARY KEY");
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
	private void showTable()
	{
		try {
			this.sqlStatement = this.sqlConnection.createStatement();
			this.sqlStatement.execute("SELECT * FROM Tenants");
			this.sqlStatement.close();
		}
		catch(SQLException ex)
		{
			System.out.println(ex);
		}
	}
	
}
