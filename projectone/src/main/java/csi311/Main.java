package csi311;

import java.io.*; // Import IO Library 

public class Main {
	
	public static void main(String [] args)
	{
		// First we need to take in command line arguments, and check to see if the argument passed in is a file.
		System.out.println(args[0]);
		try {
			if(args.length != 1)
				throw new Exception("Too many arguments. Please only specify a text input file.");
			
			if(args.length == 1)
			{
				// Check if the file exists.
				File file = new File(args[0]);
				if(file.exists())
				{
					System.out.println("File Exists!");
					FileReader fileReader = new FileReader(file);
				}
				else 
				{
					throw new FileNotFoundException("File Not Found");
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
}
