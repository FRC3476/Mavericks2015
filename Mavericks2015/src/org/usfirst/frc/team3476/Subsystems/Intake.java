package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

import edu.wpi.first.wpilibj.SpeedController;

public class Intake implements Subsystem
{
	private double SUCKMOTORSPEED, LOADMOTORSPEED;
	final String[] autoCommands = {"intake"};
	boolean done;
	SpeedController dropdown;
	SpeedController escalator;
	
	public Intake(SpeedController dropdownin, SpeedController escalatorin)
	{
		dropdown = dropdownin;
		escalator = escalatorin;
	}
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}

	@Override
	public synchronized void doAuto(double[] params, String command)
	{
		done = false;
		if(command.equalsIgnoreCase("intake"))
		{
			//Direction(sign), percent speed, constant to invert if necessary and make timing correct
			dropdown.set(params[0]*params[1]*SUCKMOTORSPEED);
			escalator.set(params[0]*params[1]*LOADMOTORSPEED);
			done = true;
		}
	}

	@Override
	public synchronized boolean isAutoDone()
	{
		return done;
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
	public synchronized void update(){}
}
