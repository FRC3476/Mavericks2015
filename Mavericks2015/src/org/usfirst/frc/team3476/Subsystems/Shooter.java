package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

import edu.wpi.first.wpilibj.SpeedController;

public class Shooter implements Subsystem
{
	final String[] autoCommands = {"shooter", "aim"};
	SpeedController fly1;
	SpeedController fly2;
	SpeedController fly3;
	SpeedController fly4;
	double flyPoint;
	
	public Shooter(SpeedController fly1in, SpeedController fly2in, SpeedController fly3in, SpeedController fly4in)
	{
		fly1 = fly1in;
		fly2 = fly2in;
		fly3 = fly3in;
		fly4 = fly4in;
		flyPoint = 0;
	}
	
	@Override
	public String[] getAutoCommands(){return new String[]{"test"};}
	
	@Override
	public void doAuto(double[] params, String command)
	{
		if(command.equalsIgnoreCase(("shooter")))
		{
			
		}
	}
	
	@Override
	public boolean isAutoDone(){return true;}
	
	@Override
	public String[] getConstantRequest()//Request all needed constants
	{
		return new String[]{"test1", "test2"};
	}
	
	@Override
	public void returnConstantRequest(double[] constants)//Get all needed constants
	{
		
	}
	
	

	@Override
	public void update()//flywheel control loop
	{
		// TODO Take back half control using flyPoint
		
	}
	
	public String toString()
	{
		return "Shooter";
	}
}
