package csi311;

import java.sql.*;
import org.apache.derby.jdbc.EmbeddedDriver;
public class Database {
	private static final String databaseURI = "jdbc:derby:ordersDB;create=true";
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
}
