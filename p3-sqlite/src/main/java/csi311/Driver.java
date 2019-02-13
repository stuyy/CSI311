package csi311;

import java.io.BufferedReader;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.State;

public class Driver {
	public static void main(String [] args) throws Exception
	{
		/*
		System.out.println(args[0] + " " + args[1]);
		Driver d = new Driver();
		String s = d.processFile(args[1]);
		System.out.println(s);
		MachineSpec m = d.parseJson(s);
		d.dumpMachine(m);
		*/
		
		Database db = new Database();
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
    /**
     * Method provided from the sample code 1.5
     * @param json - a Stringified version of the JSON objects.
     * @return a MachineSpec instance or null.
     */
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
    private void dumpMachine(MachineSpec machineSpec) {
    	if (machineSpec == null) {
    		return;
    	}
    	for (State st : machineSpec.getMachineSpec()) {
    		System.out.println(st.getState() + " : " + st.getTransitions());
    	}
    }

}
