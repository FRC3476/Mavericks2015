package org.usfirst.frc.team3476.Main;

public interface Subsystem
{
	String[] getAutoCommands();
	
	void doAuto(double[] params, String command);
	
	boolean isAutoDone();
}
