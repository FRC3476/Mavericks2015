package org.usfirst.frc.team3476.ScriptableAuto;

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
	
	public void start()
	{
		ArrayList<CommandBlock> curCommands;
		Subsystem current;
		while(par.hasNextLine())
		{
			curCommands = par.nextLine();
			while (!curCommands.isEmpty())//Keep going until line is done (ArrayList is empty)
			{
				for (CommandBlock block : curCommands)//Go thru each CommandBlock on this line
				{
					if(block.hasNext())//If there is another command, do things
					{
						current = findSubsystem(block.getCommand().getName());//Grab the subsystem that deals with this command
						if(!block.getCommand().isStarted())//If the command has not been started (new command), start it (duh)
						{
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
					}
					else//No more commands, remove that sucker
					{
						curCommands.remove(block);
					}
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
	
	private Subsystem findSubsystem(String command)
	{
		for(Subsystem toSearch: systems)
		{
			for(String searchString : toSearch.getAutoCommands())
			{
				if(searchString.equals(command)) return toSearch;
			}
		}
		
		return new ErrorSystem();
	}
}
