package csi311;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import csi311.MachineSpec.StateTransitions; 

public class ParseState implements OrderParser {
	
	private MachineSpec machineSpec;
	
	public ParseState(String machineFileDesc, String orderFileDesc) throws Exception
	{
		if(!(new File(machineFileDesc).exists()) || !(new File(orderFileDesc).exists()))
			throw new Exception("File/s do not exist.");
		else {
			//System.out.println("Both files exist. Perfect!");
			// Process the JSON file.
			String machineJSONString = processFile(machineFileDesc);
			//System.out.println(machineJSONString);
			this.machineSpec = parseJson(machineJSONString);
			boolean validState = this.isValidState("submitted");
			System.out.println(validState);
			// We're going to need a way to get the states.
			/*
			List<StateTransitions> states = this.machineSpec.getMachineSpec();
			for(StateTransitions c : states)
			{
				System.out.println("State: " + c.getState());
				List<String> transitions = c.getTransitions();
				for(String t : transitions)
					System.out.print(t + " ");
				
				System.out.println();
			}
			*/
		}
	}
	
    public void run(String machine, String orders) throws Exception {
    	System.out.println("Parse State"); 
    	
    	if (machine != null) {
    		String json = processFile(machine); // Call processFile to Stringify the JSON.
    		System.out.println("Raw json = " + json);  // Print out the JSON
    		MachineSpec machineSpec = parseJson(json); // Convert String to JSON
    		dumpMachine(machineSpec); // Print out JSON
    		
    		// We need to process the orders.
    		
    	}
    	else {
    		
    	}
    }
    
    private void processOrders(String orders) throws IOException
    {
    	// Read in the file containing the orders.
    	
    	FileReader file = new FileReader(orders);
    	BufferedReader reader = new BufferedReader(file);
    	
    	String line = "";
    	while((line = reader.readLine()) != null)
    		System.out.println(line);
    }
    
    private void dumpMachine(MachineSpec machineSpec) {
    	if (machineSpec == null) {
    		return;
    	}
    	for (StateTransitions st : machineSpec.getMachineSpec()) {
    		System.out.println(st.getState() + " : " + st.getTransitions());
    	}
    }
    
    
    private String processFile(String filename) throws Exception {
    	System.out.println("Processing file: " + filename); 
    	BufferedReader br = new BufferedReader(new FileReader(filename));  
    	String json = "";
    	String line; 
    	while ((line = br.readLine()) != null) {
    		json += " " + line; 
    	} 
    	br.close();
    	// Get rid of special characters - newlines, tabs.  
    	return json.replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", " "); 
    }

    
    private MachineSpec parseJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try { 
        	MachineSpec machineSpec = mapper.readValue(json, MachineSpec.class);
        	return machineSpec; 
        }
        catch (Exception e) {
            e.printStackTrace(); 
        }
        return null;  	
    }
    
	public boolean isValidTimestamp(String timestamp) {
		return timestamp.matches("[0-9]{13}");
	}

	public boolean isValidOrderID(String orderID) {
		return orderID.matches("[0-9]{3}\\-[a-zA-z]{3}\\-[0-9]{4}");
	}


	public boolean isValidCustomerID(String customerID) {
		return customerID.matches("[0-9]{9}");
	}

	public boolean isValidState(String state) {
		// We must check the Machine and see if it has the state that was passed in.
		List<StateTransitions> states = this.machineSpec.getMachineSpec();
		
		for(StateTransitions t : states)
		{
			if(t.getState().equalsIgnoreCase(state))
				return true;
		}
		return false;
	}


	public boolean isValidQuantity(int quantity) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isValidPrice(float price) {
		// TODO Auto-generated method stub
		return false;
	}

    /*
    public static void main(String[] args) {
    	ParseState theApp = new ParseState();
    	String machineFileName = null, ordersFileName = null;
    	if (args.length == 2) {
    		machineFileName = args[0]; 
    		ordersFileName = args[1];
    	}
    	try { 
    		theApp.run(machineFileName, ordersFileName);
    	}
    	catch (Exception e) {
    		System.out.println("Something bad happened!");
    		e.printStackTrace();
    	}
    }
	*/
}
