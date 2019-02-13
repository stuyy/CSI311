package csi311;

import java.io.BufferedReader;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.State;

public class Driver {
	public static void main(String [] args) throws Exception
	{
		try {
			if(args.length == 2)
			{
				String mode = args[0];
				String filePath = args[1];
				
				if(mode.equalsIgnoreCase("--state"))
				{
					String jsonString = FileProcessor.processFile(filePath);
					MachineSpec machineSpec = FileProcessor.parseJson(jsonString);
					FileProcessor.dumpMachine(machineSpec);
					Database db = new Database(machineSpec);
				}
				else if(mode.equalsIgnoreCase("--order"))
				{
					// Process Order
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
