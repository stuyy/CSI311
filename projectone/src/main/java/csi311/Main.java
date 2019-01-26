package csi311;

import java.io.*; // Import IO Library 

public class Main {
	
	public static void main(String [] args)
	{
		// First we need to take in command line arguments, and check to see if the argument passed in is a file.
		try {

			if(args.length == 0)
				throw new Exception("No arguments provided.");
			
			if(args.length != 1)
				throw new Exception("Too many arguments. Please only specify a text input file.");
			
			if(args.length == 1)
			{
				// Check if the file exists.
				File file = new File(args[0]);
				if(file.exists())
				{
					FileReader fileReader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String line;
					while((line = bufferedReader.readLine()) != null)
					{
						if(line.length() == 0)
							continue;
						else {
							int firstCommaPosition = line.indexOf(",");
							String packageID = line.substring(0, firstCommaPosition);
							//System.out.println("The first comma appears at " + firstCommaPosition);
							//System.out.println("Package ID: " + packageID);
							// First let's parse the Package ID.
							parsePackageID(packageID);
						}
					}
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
	
	public static Boolean parsePackageID(String packageID)
	{
		System.out.println(packageID);
		// If Package ID's length is not 12, return false.
		System.out.println(packageID.length());
		return false;
	}
}
