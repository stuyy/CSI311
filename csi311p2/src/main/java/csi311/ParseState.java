package csi311;

import java.io.BufferedReader;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.StateTransitions; 

public class ParseState {

	public ParseState() {
		
	}
	
	
    public void run(String filename) throws Exception {
    	System.out.println("Parse State");
    	if (filename != null) {
    		String json = processFile(filename); 
    		System.out.println("Raw json = " + json); 
    		MachineSpec machineSpec = parseJson(json);
    		dumpMachine(machineSpec); 
    	}
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
    	String filename = null; 
    	if (args.length > 0) {
    		filename = args[0]; 
    	}
    	try { 
    		theApp.run(filename);
    	}
    	catch (Exception e) {
    		System.out.println("Something bad happened!");
    		e.printStackTrace();
    	}
    }	
	
	

}
