package org.usfirst.frc.team3476.ScriptableAuto;

import org.usfirst.frc.team3476.Main.Subsystem;

public class ErrorSystem implements Subsystem
{
	public String[] getAutoCommands() {return new String[]{"error"};}
	
	public void doAuto(double[] params, String command) {}
	
	public boolean isAutoDone() {return true;}
}
