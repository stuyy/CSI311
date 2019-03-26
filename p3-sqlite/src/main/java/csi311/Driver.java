package csi311;

import java.io.BufferedReader;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.State;

public class Driver {
	public static void main(String [] args) throws Exception
	{
		
		Database db;
		
		try {
			if(args.length == 2)
			{
				String mode = args[0];
				String filePath = args[1];
				
				if(mode.equalsIgnoreCase("--state")) // If the arg is state, store the machine in the database.
				{
					String jsonString = FileProcessor.processFile(filePath);
					MachineSpec machineSpec = FileProcessor.parseJson(jsonString);
					db = new Database(machineSpec, jsonString);
					db.dropSchema();
					db.createSchema();
					
					db.insert();
					db.executeStatement("INSERT INTO MachineSpec VALUES ('" + jsonString + "')");
					db.displayStateMachines();
					db.displayStates();
					db.displayTenants();
					
				}
				else if(mode.equalsIgnoreCase("--order"))
				{
					// Process Order
					db = new Database();
					System.out.println("Processing orders.");
					System.out.println();
					db.displayTenants();
					System.out.println();
					db.displayStates();
					System.out.println();
					db.displayStateMachines();
					System.out.println();
					
					// You want to make sure the tenant ID that was JUST taken in from the machineSpec matches every tenantID in each line in the order file.
					// Instead of reading it from the Database, since we know the DB will store the most recent one, just take it from the machineSpec.
					
					int tenantID = db.getMachineSpec().getTentantId();
					System.out.println("Hey" + tenantID);
					
					// Now what do we do next? --order flag should probably just store all the orders in the database tbh...
					FileProcessor.processOrderFile(filePath, db);
					
					// Insert every line into the Orders table in the database. We don't need to validate it right now.
					
					
				}
				else if(mode.equalsIgnoreCase("--report"))
				{
					// Process report.
					
					// Inside report flag, we will read the orders from the Database and validate.
					db = new Database();
					db.processReport();
				}
			}
			
			else {
				throw new Exception("Invalid amount of arguments.");
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	

}
