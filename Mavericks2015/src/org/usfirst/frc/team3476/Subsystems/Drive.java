package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

public class Drive implements Subsystem
{
	private final String[] autoCommands = {"shooter", "aim", "flywheel"};
	private final String[] constants = {};
	
	private boolean done;
	
	
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}

	@Override
	public void doAuto(double[] params, String command) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAutoDone()
	{
		return done;
	}

	@Override
	public String[] getConstantRequest()
	{
		return constants;
	}

	@Override
	public void returnConstantRequest(double[] constantsin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
