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
							boolean isValidPackageID = parsePackageID(packageID.trim());
							System.out.println(isValidPackageID ? "Yes" : "No");
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
		// If Package ID's length is not 12, return false.
		if(packageID.length() != 12)
			return false;
		else {
			if(packageID.charAt(3) != '-' || packageID.charAt(7) != '-')
			{
				System.out.println("Invalid package id");
				return false;
			}
			else {
				try {
					int firstNumber = Integer.parseInt(packageID.substring(0, 3));
					int lastNumber = Integer.parseInt(packageID.substring(8, 12));
					String middle = packageID.substring(4, 7);
					boolean hasADigit = hasDigit(middle);
					if(hasADigit)
						return false;
					else
						return true;
				}
				catch(Exception ex)
				{
					return false;
				}
			}
		}
	}
	
	public static Boolean hasDigit(String str)
	{
		int i = 0;
		while(i < str.length()){
			if(Character.isDigit(str.charAt(i)))
				return true;
			i++;
		}
		
		return false;
		
	}
}
