package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class Shooter implements Subsystem
{
	private final String[] autoCommands = {"shooter", "aim", "flywheel"};
	private final String[] constants = {"AIMUPPOWERED"};
	private boolean AIMUPPOWERED, done;
	private SpeedController fly1, fly2, fly3, fly4;
	private Solenoid aim;
	private double flyPoint;
	private Thread flyThread;
	
	public Shooter(SpeedController fly1in, SpeedController fly2in, SpeedController fly3in, SpeedController fly4in, Solenoid aimin)
	{
		fly1 = fly1in;
		fly2 = fly2in;
		fly3 = fly3in;
		fly4 = fly4in;
		aim = aimin;
		done = true;
		flyPoint = 0;
		flyThread = new Thread(new SubsystemTask(this));
		flyThread.start();
	}
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}
	
	@Override
	public void doAuto(double[] params, String command)
	{
		done = false;
		boolean up = params[0] == 1;
		switch(command)
		{
			case "shooter":
				flyPoint = params[1];
				aim.set(AIMUPPOWERED ? up : !up);
				break;
			case "aim":
				aim.set(AIMUPPOWERED ? up : !up);
				break;
			case "flywheel":
				flyPoint = params[1];
				break;
		}
	}
	
	@Override
	public boolean isAutoDone()
	{
		return done;
	}
	
	@Override
	public String[] getConstantRequest()//Request all needed constants
	{
		return constants;
	}
	
	@Override
	public void returnConstantRequest(double[] constantsin)//Get all needed constants
	{
		AIMUPPOWERED = constantsin[0] == 1 ? true : false;
	}

	@Override
	public void update()//flywheel control loop
	{
		//TODO: Take back half control using flyPoint
		double output = 0;
		
		
		
		fly1.set(output);
		fly2.set(output);
		fly3.set(output);
		fly4.set(output);
	}
	
	public String toString()
	{
		return "Shooter";
	}
}
