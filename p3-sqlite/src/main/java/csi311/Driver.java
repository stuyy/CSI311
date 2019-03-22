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
				

				String jsonString = FileProcessor.processFile(filePath);
				MachineSpec machineSpec = FileProcessor.parseJson(jsonString);
				
				if(mode.equalsIgnoreCase("--state")) // If the arg is state, store the machine in the database.
				{
					db = new Database(machineSpec);
					db.dropSchema();
					db.createSchema();
					
					db.insert();
					db.displayStateMachines();
					db.displayStates();
					db.displayTenants();
					
				}
				else if(mode.equalsIgnoreCase("--order"))
				{
					// Process Order
					db = new Database(machineSpec);
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
					// Now what do we do next? --order flag should probably just store all the orders in the database tbh...
					
					
				}
				else if(mode.equalsIgnoreCase("--report"))
				{
					// Process report.
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
