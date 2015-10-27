package org.usfirst.frc.team3476.Utility.Control;

import edu.wpi.first.wpilibj.PIDOutput;

public class PIDOutputWrapper implements PIDOutput
{
	private double out;
	
	public PIDOutputWrapper()
	{
		out = 0;
	}
	
	@Override
	public void pidWrite(double output)
	{
		out = output;
	}
	
	public double getOutput()
	{
		return out;
	}
}
