package csi311;

import java.io.*;
import java.util.ArrayList;

public class PackageParser {
	
	private ArrayList<String> invalidPackages;
	
	public PackageParser()
	{
		this.invalidPackages = new ArrayList<>();
	}
	
	public ArrayList<String> getInvalidPackages()
	{
		return this.invalidPackages;
	}
	
	public boolean getCommaCount(String currLine)
	{
		int i = 0, commaCount = 0;
		while(i < currLine.length()) {
			if(currLine.charAt(i) == ',')
				commaCount++;
			
			i++;
		}
		
		return commaCount == 2;
	}
	public boolean lineParser(String line) throws Exception
	{
		// Leading whitespace and trailing whitespace has already been trimmed.
		int i = 0;
		int currentPosition = 0;
		while(i < line.length())
		{
			if(currentPosition < 3)
			{
				if(Character.isDigit(line.charAt(i)))
				{
					currentPosition++;
					i++;
				}
				else if(Character.isWhitespace(line.charAt(i)))
					throw new Exception("Invalid");
				
				else if(!Character.isDigit(line.charAt(i)))
					throw new Exception("Invalid");
			}
			else if(currentPosition == 3 || currentPosition == 7)
			{
				if(line.charAt(i) == '-')
				{
					currentPosition++;
					i++;
				}
				else if(Character.isWhitespace(line.charAt(i)))
					throw new Exception("Invalid");
				else
					throw new Exception("Invalid");
			}
			else if(currentPosition > 3 && currentPosition < 7)
			{
				if(Character.isLetter(line.charAt(i)))
				{
					currentPosition++;
					i++;
				}
				else if(Character.isWhitespace(line.charAt(i)))
					throw new Exception("Invalid");
				else
					throw new Exception("Invalid");
			}
			else if(currentPosition > 7 && currentPosition < 12)
			{
				if(Character.isDigit(line.charAt(i)))
				{
					currentPosition++;
					i++;
				}
				else if(Character.isWhitespace(line.charAt(i)))
					throw new Exception("Invalid");
				
				else if(!Character.isDigit(line.charAt(i)))
				{
					throw new Exception("Invalid");
				}
			}
			else if(currentPosition > 12)
				throw new Exception("Invalid Package Id.");
		}
		return true;
	}
}
