package org.usfirst.frc.team3476.Utility.Control;

import edu.wpi.first.wpilibj.Timer;

public class TakeBackHalf extends ControlLoop
{
	private double lastTBH, integral, lastTime, lastProcess, gain;
	private Timer timer;
	
	
	public TakeBackHalf(double[] outputrangein, double gainin)
	{
		super(outputrangein);
		lastTBH = 0;
		integral = 0;
		gain = gainin;
		timer = new Timer();
		timer.start();
	}
	
	public void setGain(double gainin)
	{
		gain = gainin;
	}

	@Override
	protected double run(double process)
	{
		double setpoint = getSetpoint();
		double curTime = timer.get();
		
		integral = integral + (curTime - lastTime)*(setpoint - process);
		if((setpoint - lastProcess)*(setpoint - process) < 0)//Change of signs means overshoot
		{
			integral = (lastTBH + integral)/2;
			lastTBH = integral;
		}
		
		lastTime = curTime;
		lastProcess = process;
		return integral*gain;
	}
}
