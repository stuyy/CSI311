package csi311;

import java.io.*; // Import IO Library 

public class MainOld {
	
	private String [] invalidPackages;
	public static void main(String [] args)
	{
		
		// We need a data structure to keep track of ALL valid packages.
		
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
							
							// Now we need to check if there are any extra commas right after the first one.
							
							String next = line.substring(firstCommaPosition);
							System.out.println(next);
							// From here, we must count the number of commas in this substring.
							// If there is more than 1 comma, we must reject.
							// Some pseudocode, start with charAt(i) where i = 0, and we loop until the character is not a space, counting every comma.
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
		
		getCommas(",, ,,,,,,,     ,,,,, 1");
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
	
	public static int getCommas(String str)
	{
		// We take the input string, which is a substring of the line read. 
		if (str.length() == 0)
			return -1;
		int i = 0;
		int commas = 0;
		// We only care about commas and spaces.
		while(i < str.length())
		{
			if(str.charAt(i) == ',')
				commas++;
			
			else if(str.charAt(i) != ' ')
				break;
			i++;
		}
		System.out.println(commas);
		return commas;
	}
}
