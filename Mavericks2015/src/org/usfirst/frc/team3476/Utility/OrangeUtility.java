package org.usfirst.frc.team3476.Utility;

public class OrangeUtility
{
	public static double coerce(double toCoerce, double high, double low)
	{
		if(toCoerce > high)
		{
			return high;
		}
		else if(toCoerce < low)
		{
			return low;
		}
		return toCoerce;
	}
}
