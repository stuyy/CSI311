package csi311;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws Exception
	{
		PackageParser parser = new PackageParser();
		try {
			File file = new File(args[0]);
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
						parser.getInvalidPackages().add(currentLine.substring(0, currentLine.indexOf(',')));
						continue;
					}
					
					boolean validCommaCount = parser.getCommaCount(currentLine);
					String packageID = currentLine.substring(0, currentLine.indexOf(',')).trim();
					
					if(validCommaCount) // If there are only 2 commas, then we go into this case.
					{
						// Parse the ID. Get the Substring of the currentLine to retrieve the package ID,
						// and trim any leading or trailing whitespace.
						try {
							
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
									//System.out.println(address);
									String team = parser.validateTeam(address);
									if(team != null)
									{
										if(Double.parseDouble(weight) > 50)
										{
											
											parser.incrementExceededPackages();
										}
										else { // Package is less than 50, and the team is valid.
											//System.out.println("Team: " + team + "\n" + address);
											if(parser.getValidPackages().containsKey(team))
											{
												int currentCount = parser.getValidPackages().get(team);
												parser.getValidPackages().put(team, ++currentCount);
											}
											else
												parser.getValidPackages().put(team, 1);
										}
									}
									else
									{
										parser.getInvalidPackages().add(packageID);
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
							parser.getInvalidPackages().add(packageID);
						}
					}
					else
						parser.getInvalidPackages().add(packageID);
				}
				
				System.out.println("Invalid Packages:");
				System.out.println(parser.getInvalidPackages());
				
				Iterator iter = parser.getValidPackages().entrySet().iterator();
				while(iter.hasNext())
				{
					Map.Entry value = (Map.Entry)iter.next(); 
					System.out.println(value.getKey() + ": " + value.getValue());
				}
				System.out.println(">50lbs : " + parser.getExceededPackages());
			}
			else
				throw new Exception("Invalid File or File does not exist.");
		}
		catch(Exception ex)
		{
			System.out.println("Error. No arguments provided.");
		}
	}
}
