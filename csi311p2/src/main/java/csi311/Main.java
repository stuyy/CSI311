package csi311;

public class Main {
    public static void main(String[] args) {
    	
    	try { 
    		if(args.length == 2)
    		{
    			ParseState app = new ParseState(args[0], args[1]);
    		}
    		else
    			throw new Exception("Invalid amount of arguments. Must specify a <MachineSpec> <OrderFileDesc>");
    	}
    	catch (Exception e) {
    		System.out.println(e);
    		e.printStackTrace();
    	}
    }
}
