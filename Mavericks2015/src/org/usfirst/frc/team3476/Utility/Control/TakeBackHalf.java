package org.usfirst.frc.team3476.Utility.Control;

import edu.wpi.first.wpilibj.Timer;

public class TakeBackHalf extends ControlLoop
{
	private double lastTBH, integral, lastTime;
	private Timer timer;
	
	
	public TakeBackHalf(double[] outputrangein)
	{
		super(outputrangein);
		lastTBH = 0;
		integral = 0;
		timer = new Timer();
		timer.start();
	}

	@Override
	protected double run(double process)
	{
		double curTime = timer.get();
		
		integral = integral + (curTime - lastTime)*(getSetpoint() - process);
		
				
				
		lastTime = curTime;
		return 0;
	}
}
