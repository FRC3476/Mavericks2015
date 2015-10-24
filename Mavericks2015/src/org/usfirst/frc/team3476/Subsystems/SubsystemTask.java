package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;

public class SubsystemTask implements Runnable
{
	Subsystem system;
	
	public SubsystemTask(Subsystem systemin)
	{
		system = systemin;
	}
	
	@Override
	public void run()
	{
		system.update();
	}

}
