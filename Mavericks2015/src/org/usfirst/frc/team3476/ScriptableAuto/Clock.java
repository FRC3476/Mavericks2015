package org.usfirst.frc.team3476.ScriptableAuto;

import org.usfirst.frc.team3476.Main.Subsystem;
import org.usfirst.frc.team3476.Subsystems.SubsystemTask;

public class Clock implements Subsystem
{
	private Thread clockThread;
	private SubsystemTask task;
	
	private double time;
	private boolean done;
	
	public Clock()
	{
		done = true;
		time = 1;
		
		task = new SubsystemTask(this);
		clockThread = new Thread(task, "Clock");
		clockThread.start();
	}
	
	@Override
	public String[] getAutoCommands()
	{
		return new String[]{"wait"};
	}

	@Override
	public void doAuto(double[] params, String command)
	{
		done = false;
		if(command.equalsIgnoreCase("wait"))
		{
			wait(params[0]);
		}
	}

	@Override
	public boolean isAutoDone()
	{
		return done;
	}

	@Override
	public String[] getConstantRequest(){return null;}

	@Override
	public void returnConstantRequest(double[] constantsin){}

	@Override
	public void update()
	{
		if(!done)
		{
			try
			{
				clockThread.wait((int)(time*1000));
				done = true;
			}
			catch (InterruptedException e){}
		}
	}

	@Override
	public void stopThreads()
	{
		task.hold();
	}

	@Override
	public void startThreads()
	{
		task.resume();
	}

	@Override
	public void terminateThreads()
	{
		task.terminate();
		try
		{
			clockThread.join();
			System.out.println("Ended " + this + " thread.");
		}
		catch(InterruptedException e)
		{
			System.out.println("Ended " + this + " thread.");
		}
	}
	
	public void wait(double timein)
	{
		time = timein;
	}
	
	@Override
	public String toString()
	{
		return "Clock";
	}
}
