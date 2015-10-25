package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

public class Intake implements Subsystem
{
	private double SUCKMOTORSPEED, LOADMOTORSPEED;
	final String[] autoCommands = {"intake"};
	boolean done;
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}

	@Override
	public synchronized void doAuto(double[] params, String command)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized boolean isAutoDone()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getConstantRequest()
	{
		return new String[]{"SUCKMOTORSPEED", "LOADMOTORSPEED"};
	}

	@Override
	public void returnConstantRequest(double[] constants)
	{
		SUCKMOTORSPEED = constants[0];
		LOADMOTORSPEED = constants[1];
	}

	@Override
	public synchronized void update()
	{
		// TODO Auto-generated method stub
		
	}

}
