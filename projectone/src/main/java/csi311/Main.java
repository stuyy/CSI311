package csi311;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {
	public static void main(String[] args) throws Exception
	{
		PackageParser parser = new PackageParser();
		String fileName = "C:/Users/Anson/Desktop/test.txt";
		File file = new File(fileName);
		if(file.exists() || !file.getName().endsWith(".txt")) // Check if file exists.
		{
			FileReader currFile = new FileReader(file);
			BufferedReader reader = new BufferedReader(currFile);
			
			String currentLine;
			while((currentLine = reader.readLine()) != null)
			{
				if(currentLine.length() == 0) // If the line is empty, continue.
					continue;
				
				if(currentLine.trim().endsWith(",")) // If the line ends with a comma, continue.
				{
					//System.out.println("Invalid Line, comma at the wrong position");
					parser.getInvalidPackages().add(currentLine);
					continue;
				}
				
				boolean validCommaCount = parser.getCommaCount(currentLine);
				
				if(validCommaCount) // If there are only 2 commas, then we go into this case.
				{
					// Parse the ID. Get the Substring of the currentLine to retrieve the package ID,
					// and trim any leading or trailing whitespace.
					try {
						String packageID = currentLine.substring(0, currentLine.indexOf(',')).trim();
						boolean isValidPackageID = parser.lineParser(packageID);
						if(isValidPackageID) // if the package id is valid
						{
							// Now check the next substring to see if it has a valid street/avenue name.
							int firstCommaPosition = currentLine.indexOf(',');
							String address = currentLine.substring(firstCommaPosition+1).trim();

							String weight = address.substring(address.indexOf(',')+1).trim();
							address = address.substring(0, address.indexOf(','));
							boolean isValidAddress = parser.validateAddress(address);
							
							if(isValidAddress)
							{
								// Check for the team.
								System.out.println(address);
								boolean validTeam = parser.validateTeam(address);
								if(validTeam)
								{
									//if(weight > 50)
										
								}
							}
							else {
								throw new Exception("Invalid Address");
							}
							// Then check if the weight is good.
						}
					}
					catch(Exception ex)
					{
						parser.getInvalidPackages().add(currentLine);
					}
				}
				else
					parser.getInvalidPackages().add(currentLine);
			}
			
			System.out.println("Invalid Packages:");
			for(String c : parser.getInvalidPackages())
				System.out.println(c);
		}
		else
			throw new Exception("Invalid File or File does not exist.");
	}
}
