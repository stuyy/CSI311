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
}
