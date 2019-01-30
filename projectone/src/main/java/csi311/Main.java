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
		// Check if file exists.
		if(file.exists() || !file.getName().endsWith(".txt"))
		{
			FileReader currFile = new FileReader(file);
			BufferedReader reader = new BufferedReader(currFile);
			
			String currentLine;
			while((currentLine = reader.readLine()) != null)
			{
				if(currentLine.length() == 0)
					continue;
				
				if(currentLine.trim().endsWith(","))
				{
					//System.out.println("Invalid Line, comma at the wrong position");
					parser.getInvalidPackages().add(currentLine);
					continue;
				}
				
				boolean validCommaCount = parser.getCommaCount(currentLine);
				if(validCommaCount)
				{
					// Parse the ID. Get the Substring of the currentLine to retrieve the package ID,
					// and trim any leading or trailing whitespace.
					try {
						String packageID = currentLine.substring(0, currentLine.indexOf(',')).trim();
						boolean isValidPackageID = parser.lineParser(packageID);
						if(isValidPackageID)
						{
							// Now check the next substring to see if it has a valid street/avenue name.
							
							// Then check if the weight is good.
						}
					}
					catch(Exception ex)
					{
						parser.getInvalidPackages().add(currentLine);
					}
				}
				else
				{
					System.out.println("This line does not have valid commas");
					parser.getInvalidPackages().add(currentLine);
				}
			}
			System.out.println("Invalid Packages:");
			for(String c : parser.getInvalidPackages())
				System.out.println(c);
		}
		else
			throw new Exception("Invalid File or File does not exist.");
	}
}
