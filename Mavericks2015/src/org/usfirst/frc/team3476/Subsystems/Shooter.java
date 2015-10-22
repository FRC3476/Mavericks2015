package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

public class Shooter implements Subsystem
{
	double test1, test2;
	
	public String[] getAutoCommands(){return new String[]{};}
	
	public void doAuto(double[] params, String command)
	{
		if(command.equals("test"))
		{
			System.out.println("Test echo: colon param: " + params[0] + " @ param: " + params[1] + ". Constants: " + test1 + ", " + test2);
		}
	}
	
	public boolean isAutoDone(){return true;}
	
	public String[] getConstantRequest(){return new String[]{"test1", "test2"};}//Request all needed constants
	
	public void returnConstantRequest(double[] constants)
	{
		test1 = constants[0];
		test2 = constants[1];
	}//Request all needed constants
}
