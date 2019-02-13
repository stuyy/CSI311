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
	
	private void showTable()
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
		db.showTable();
		
	}
	
}
