package org.usfirst.frc.team3476.ScriptableAuto;

import org.usfirst.frc.team3476.Subsystems.Shooter;
import org.usfirst.frc.team3476.Main.Subsystem;

public class AutoTester
{
	public static void main(String[] args)
	{
		String script = "test: 47@48 \ntest";
		
		String constants = "2016~test1 = 3.0\ntest2=4.0\00272015~test1 = 1.0\ntest2=2.0";
		
		Subsystem[] systems = new Subsystem[]{new Shooter()};
		
		Main main = new Main("2015", systems, script, constants);
		
		System.out.println("starting");
		main.start();
		System.out.println("done");
	}
}
