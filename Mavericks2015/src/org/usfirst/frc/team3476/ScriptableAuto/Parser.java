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
		System.out.println("Parsing line.");
		System.out.println("SCRIPT: \"" + script + "\"");
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
		if (script.length() == endOfLine)
		{
			script = "";//Remove the line we just retrieved
		}
		else
		{
			script = script.substring(endOfLine + 1);
		}
		
		//Remove comments
		int comdex = line.indexOf("//");
		if(comdex != -1)
		{
			line = line.substring(0, comdex);
		}
		
		ArrayList<CommandBlock> lineCommands = new ArrayList<CommandBlock>();
		
		int semiIndex = line.indexOf(PARALLELSEPARATOR);
		while(semiIndex != -1)//While there are still parallel commands to be processed, create command blocks
		{
			line = line.trim();
			String commandBlock = line.substring(0, semiIndex);
			line = line.substring(semiIndex + 1);
			
			lineCommands.add(parseCommandBlock(commandBlock));
			
			semiIndex = line.indexOf(PARALLELSEPARATOR);
		}
		lineCommands.add(parseCommandBlock(line));
		
		return lineCommands;
	}
	
	private CommandBlock parseCommandBlock(String commandBlock)
	{
		System.out.println("Parsing CommandBlock.");
		commandBlock = commandBlock.trim();
		ArrayList<Command> blockCommands = new ArrayList<Command>();
		
		int thendex = commandBlock.indexOf("then");
		while(thendex != -1)
		{
			commandBlock = commandBlock.trim();
			String command = commandBlock.substring(0, thendex);
			commandBlock = commandBlock.substring(thendex + 1);
			
			blockCommands.add(parseCommand(command));
			
			thendex = commandBlock.indexOf("then");
		}
		blockCommands.add(parseCommand(commandBlock));
		
		return new CommandBlock(blockCommands);
	}
	
	private Command parseCommand(String thenBlock)
	{
		System.out.println("Parsing Command.");
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
		System.out.println("command parsed: " + new Command(command, params));
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
			if(possYear.substring(0, possYear.indexOf(YEARSEPERATOR)).equals(constantYear))
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
