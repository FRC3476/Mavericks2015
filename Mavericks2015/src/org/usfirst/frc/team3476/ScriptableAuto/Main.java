package org.usfirst.frc.team3476.ScriptableAuto;

import java.io.IOException;
import java.util.ArrayList;

import org.usfirst.frc.team3476.Main.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Main
{
	Parser par;
	Subsystem[] systems;
	
	public Main(String year, Subsystem[] systemsin)
	{
		par = new Parser(getScript(), getConstants(), year);
		systems = systemsin;
	}
	
	//Testing constructor
	public Main(String year, Subsystem[] systemsin, String script, String constants)
	{
		par = new Parser(script, constants, year);
		systems = systemsin;
	}
	
	public void start()
	{
		ArrayList<CommandBlock> curCommands;
		Subsystem current;
		boolean done;
		
		while(par.hasNextLine())
		{
			done = false;
			curCommands = par.nextLine();
			while (!done)//Keep going until line is done (ArrayList is empty)
			{
				done = true;
				for (CommandBlock block : curCommands)//Go thru each CommandBlock on this line
				{
					if(block.hasNext())//If there is another command, do things
					{
						current = findSubsystem(block.getCommand());//Grab the subsystem that deals with this command
						if(!block.getCommand().isStarted())//If the command has not been started (new command), start it (duh)
						{							
							//Return requested constants to the subsystem
							String[] request = current.getConstantRequest();
							double[] response = new double[request.length];
							try
							{
								for(int i = 0; i < request.length; i++)
								{
									response[i] = par.getConstant(request[i]);
								}
								current.returnConstantRequest(response);
							}
							catch (IOException e)
							{
								for(int i = 0; i < response.length; i++) response[i] = 0.0;
								current.returnConstantRequest(response);
								System.out.println("IOEXCEPTION: " + e.getMessage());
							}
							current.doAuto(block.getCommand().getParams(), block.getCommand().getName());
							block.getCommand().start();
						}
						else
						{
							if(current.isAutoDone())//If the subsystem is done, remove the command from the queue
							{
								block.finishCommand();
							}
						}
						done = false;
					}
					else{/*No more commands, leave it alone, we are iterating over this ArrayList - throws ConcurrentModificationException*/}
				}
			}
		}
	}
	
	private String getScript()
	{
		return SmartDashboard.getString("java auto text");
	}
	
	private String getConstants()
	{
		return SmartDashboard.getString("java constants");
	}
	
	private Subsystem findSubsystem(Command command)
	{
		for(Subsystem toSearch : systems)
		{
			for(String searchString : toSearch.getAutoCommands())
			{
				if(searchString.equals(command.getName())) return toSearch;
			}
		}
		
		return new ErrorSystem();
	}
}
