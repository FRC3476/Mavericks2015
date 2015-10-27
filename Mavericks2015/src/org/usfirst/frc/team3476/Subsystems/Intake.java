package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

import edu.wpi.first.wpilibj.SpeedController;

public class Intake implements Subsystem
{
	private double SUCKMOTORSPEED, LOADMOTORSPEED;
	final String[] autoCommands = {"intake"};
	final String[] constants = {"SUCKMOTORSPEED", "LOADMOTORSPEED"};
	private boolean done;
	private SpeedController dropdown;
	private SpeedController escalator;
	
	public Intake(SpeedController dropdownin, SpeedController escalatorin)
	{
		dropdown = dropdownin;
		escalator = escalatorin;
		done = true;
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
			//Direction(sign(possibly 0)), percent speed, constant to invert if necessary and make timing correct
			dropdown.set(params[0]*params[1]*SUCKMOTORSPEED/100);
			escalator.set(params[0]*params[1]*LOADMOTORSPEED/100);
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
		return constants;
	}

	@Override
	public synchronized void returnConstantRequest(double[] constantsin)
	{
		SUCKMOTORSPEED = constantsin[0];
		LOADMOTORSPEED = constantsin[1];
	}

	@Override
	public synchronized void update(){}
}
