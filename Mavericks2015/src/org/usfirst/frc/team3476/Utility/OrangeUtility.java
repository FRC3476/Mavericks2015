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
	
	public static double normalize(double toNormalize, double fromHigh, double fromLow, double toHigh, double toLow)
	{
		double factor = (toHigh - toLow) / (fromHigh - fromLow);
		double add = toLow - fromLow*factor;
		return toNormalize*factor + add;
	}
	
	public static boolean isStartLegal(Thread testing)
	{
		return testing.getState() == Thread.State.NEW;
	}
}
