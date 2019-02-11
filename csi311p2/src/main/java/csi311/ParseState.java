package csi311;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.StateTransitions; 

public class ParseState {
	
	public ParseState() {
		
	}
	
	
    public void run(String machine, String orders) throws Exception {
    	System.out.println("Parse State"); 
    	
    	if (machine != null) {
    		String json = processFile(machine); // Call processFile to Stringify the JSON.
    		System.out.println("Raw json = " + json);  // Print out the JSON
    		MachineSpec machineSpec = parseJson(json); // Convert String to JSON
    		dumpMachine(machineSpec); // Print out JSON
    		
    		// We need to process the orders.
    		processOrders(orders);
    	}
    	else {
    		
    	}
    }
    
    private static void processOrders(String orders) throws IOException
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
	
}
