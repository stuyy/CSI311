package csi311;

import java.io.BufferedReader;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.State;

public class Driver {
	public static void main(String [] args) throws Exception
	{
		
		Database db = new Database();
		db.createSchema(); // Upon running the application, we first initialize the database by creating all of the necessary tables.
		
		// After initializing the database, we shall check to see which mode we are running our application in.
		try {
			if(args.length != 2)
				throw new Exception("Invalid amount of arguments");
			else {
				String appMode = args[0]; // Store the mode
				String file = args[1]; // Store the file path.
				
				int tenantArg;
				
				if(appMode.equalsIgnoreCase("--state")) // If --state, load the state machine into the database table.
				{
					String json = FileProcessor.processFile(file);
					MachineSpec ms = FileProcessor.parseJson(json);
					// Now insert the state machine into the database.
					
					if(db.tenantExists(ms.getTentantId())) // Check if the StateMachine already exists.
					{
						System.out.println("The Machine exists for tenant " + ms.getTentantId());
					}
					else {
						db.insertStateMachine(ms, json);
					}
					
					db.displayStateMachines();
				}
				else if(appMode.equalsIgnoreCase("--order"))
				{
					// Now that we know we can store multiple state machines mapped by a tenantID, we can store orders inside the db.
					FileProcessor.processOrderFile(file, db);
				}
				else if(appMode.equalsIgnoreCase("--report"))
				{
					// Here, we will process a report with the corresponding tenantID.
					tenantArg = Integer.parseInt(args[1]);
					System.out.println("Processing report for " + tenantArg);
					db.processReport(tenantArg);
				}
			}										
		}
		catch(NumberFormatException ex)
		{
			System.out.println("Invalid tenant id. Must be an integer.");
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		
	}
	
	// --order C:\Users\Anson\Desktop\p3orders.txt
	
}
