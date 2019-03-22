package csi311;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.sqlite.*;
public class Test {
	
	Connection connection;
	Statement statement;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dbConnectionURI = "jdbc:sqlite:test.db"; 
		
		Test test =  new Test();
		
		try {
			Class.forName("org.sqlite.JDBC");
			test.connection = DriverManager.getConnection(dbConnectionURI);
			System.out.println("Successfully connected to the database.");
			test.executeStatement("Create table IF NOT EXISTS Tenants (tenantId INTEGER NOT NULL PRIMARY KEY)");
			test.executeStatement("INSERT INTO Tenants VALUES (1222345)");
		}
		catch(Exception ex)
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
