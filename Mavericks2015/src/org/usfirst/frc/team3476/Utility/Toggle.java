package org.usfirst.frc.team3476.Utility;

public class Toggle
{
	boolean rising, last, out;
	
	public Toggle(boolean rise)
	{
		rising = rise;
		last = false;
		out = false;
	}
	
	public Toggle()
	{
		rising = true;
		last = false;
		out = false;
	}
	
	public boolean get()
	{
		return out;
	}
	
	public void input(boolean in)
	{
		if(!rising)
		{
			inputFalling(in);
		}
		else
		{
			inputRising(in);
		}
	}
	
	private void inputRising(boolean in)
	{
		if(in && !last)
		{
			out = !out;
		}
	}
	
	private void inputFalling(boolean in)
	{
		if(!in && last)
		{
			out = !out;
		}
	}
}