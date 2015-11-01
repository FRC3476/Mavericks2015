package org.usfirst.frc.team3476.Utility.Control;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDSource;

public class DifferentialGyro extends Gyro implements PIDSource
{
	public DifferentialGyro(int channel){super(channel);}
	
	public DifferentialGyro(AnalogInput channel){super(channel);}

	private double lastHeading;
	
	public double calcDiff()
	{
		return getAngle() - lastHeading;
	}
	
	public double getAngle()
	{
		return getAngle() - lastHeading;
	}
	
	public void reset()
	{
		lastHeading = getAngle();
	}
	
	@Override
	public double pidGet()
	{
		return calcDiff();
	}

}
