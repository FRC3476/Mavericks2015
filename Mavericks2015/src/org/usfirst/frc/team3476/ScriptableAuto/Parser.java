package org.usfirst.frc.team3476.ScriptableAuto;

import java.util.ArrayList;

public class Parser
{
	String PARALLELSEPARATOR = ";", CONSTANTSSEPERATOR = "\0027", YEARSEPERATOR = "~";
	String script;
	String constants;
	String constantYear;
	
	public Parser(String scriptin, String constantsin, String thisYear)
	{
		script = scriptin;
		constantYear = thisYear;
		constants = retrieveThisYear(constantsin);
	}
	
	public ArrayList<CommandBlock> nextLine()
	{
		if(script.equals(""))
		{
			return new ArrayList<CommandBlock>();
		}
		int endOfLine = script.indexOf("\n");
		if(endOfLine == -1)
		{
			endOfLine = script.length();
		}
		String line = script.substring(0, endOfLine);//Get the next line
		script = script.substring(endOfLine + 1);//Remove the line we just retrieved
		line = line.substring(0, line.indexOf("//"));//Remove comments
		
		ArrayList<CommandBlock> lineCommands = new ArrayList<CommandBlock>();
		
		while(line.indexOf(PARALLELSEPARATOR) != -1)//While there are still parallel commands to be processed, create command blocks
		{
			line = line.trim();
			String commandBlock = line.substring(0, line.indexOf(";"));
			line = line.substring(line.indexOf(";") + 1);
			commandBlock = commandBlock.trim();
			
			lineCommands.add(parseCommandBlock(commandBlock));
		}
		return lineCommands;
	}
	
	private CommandBlock parseCommandBlock(String commandBlock)
	{
		commandBlock = commandBlock.trim();
		ArrayList<Command> blockCommands = new ArrayList<Command>();
		int thendex = commandBlock.indexOf("then");
		while(thendex != -1)
		{
			blockCommands.add(parseCommand(commandBlock.substring(0, thendex)));
			thendex = commandBlock.indexOf("then");
		}
		return new CommandBlock(blockCommands);
	}
	
	private Command parseCommand(String thenBlock)
	{
		thenBlock = thenBlock.trim();
		String command = "error";
		double colonParam = 0.0;
		double atParam = 0.0;
		int colonIndex = thenBlock.indexOf(":");
		int atIndex = thenBlock.indexOf("@");
		if(colonIndex != -1)//Is there a colon?
		{
			command = thenBlock.substring(0, colonIndex).trim();
			if(atIndex != -1)//Is there an at?
			{
				colonParam = cleanDoubleParse(command.substring(colonIndex + 1, atIndex)); //Grab the stuff between the : and @ and parse
				atParam = cleanDoubleParse(command.substring(atIndex + 1)); //Grab the stuff after the @ and parse
			}
			else
			{
				colonParam = cleanDoubleParse(command.substring(colonIndex + 1)); //Grab the stuff after the : - no @
			}
		}
		else
		{
			command = thenBlock;
		}
		
		//Construct command from parsed command block
		double[] params = new double[2];
		params[0] = colonParam;
		params[1] = atParam;
		return new Command(command, params);
	}
	
	public double getConstant(String key)
	{
		int keydex = constants.indexOf(key);
		String sValue = constants.substring(constants.indexOf("=", keydex) + 1, constants.indexOf("\n", keydex)).trim();
		return cleanDoubleParse(sValue);
	}
	
	private String retrieveThisYear(String constantsin)
	{
		ArrayList<String> years = new ArrayList<String>();
		int sep;
		while(constantsin.indexOf(CONSTANTSSEPERATOR) != -1)
		{
			sep = constantsin.indexOf(CONSTANTSSEPERATOR);
			years.add(constantsin.substring(0, sep));
			constantsin = constantsin.substring(sep + 1);
		}
		
		for(String possYear : years)
		{
			if(possYear.substring(0, possYear.indexOf("YEARSEPERATOR")).equals(constantYear))
			{
				return possYear;
			}
		}
		return "";
	}
	
	private double cleanDoubleParse(String mess)
	{
		return Double.parseDouble(mess.replaceAll("[^\\D.-]", ""));
	}
	
	public boolean hasNextLine()
	{
		return !script.equals("");
	}
}
