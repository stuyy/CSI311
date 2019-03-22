package csi311;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import csi311.MachineSpec.State;
public class FileProcessor {
	
	public static String processFile(String filename) throws Exception {
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
	public static MachineSpec parseJson(String json) {
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
    public static void dumpMachine(MachineSpec machineSpec) {
    	if (machineSpec == null) {
    		return;
    	}
    	for (State st : machineSpec.getMachineSpec()) {
    		System.out.println(st.getState() + " : " + st.getTransitions());
    	}
    }
    
    public static void processOrderFile(String filename) throws Exception {
    	File file = new File(filename);
    	if(!file.exists())
    		throw new Exception("Error.");
    	else {
    		System.out.println("File exists.");
    		BufferedReader reader = new BufferedReader(new FileReader(file));
    		String line;
    		
    		while((line = reader.readLine()) != null)
    		{
    			System.out.println(line);
    			
    		}
    	}
    }
}
