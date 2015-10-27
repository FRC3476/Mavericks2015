package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;
import org.usfirst.frc.team3476.Utility.Control.TakeBackHalf;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;

public class Shooter implements Subsystem
{
	private final String[] autoCommands = {"shooter", "aim", "flywheel"};
	private final String[] constants = {"AIMUPPOWERED", "SHOOTEROUTPUTRANGEHIGH", "SHOOTEROUTPUTRANGELOW", "SHOOTERIGAIN", "FLY1DIR", "FLY2DIR", "FLY3DIR", "FLY4DIR", "GRABFRISBEETIME", "SHOOTFRISBEETIME"};
	private final double RPMTORPS = 60;
	
	enum Aim{UP, DOWN}
	enum Load{IN, OUT}
	
	private double SHOOTEROUTPUTRANGEHIGH, SHOOTEROUTPUTRANGELOW, SHOOTERIGAIN, GRABFRISBEETIME, SHOOTFRISBEETIME;
	private double[] FLYDIRS;
	private boolean AIMUPPOWERED, done, firing, firingLast;
	private SpeedController fly1, fly2, fly3, fly4;
	private Solenoid aim, loader;
	private Thread flyThread;
	private TakeBackHalf control;
	private Counter tach;
	private Timer shootingTimer;
	
	public Shooter(SpeedController fly1in, SpeedController fly2in, SpeedController fly3in, SpeedController fly4in, Solenoid aimin, Solenoid loaderin, Counter tachin)
	{
		fly1 = fly1in;
		fly2 = fly2in;
		fly3 = fly3in;
		fly4 = fly4in;
		aim = aimin;
		done = true;
		tach = tachin;
		loader = loaderin;
		firing = false;
		shootingTimer = new Timer();
		flyThread = new Thread(new SubsystemTask(this));
	}
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}
	
	@Override
	public synchronized void doAuto(double[] params, String command)
	{
		done = false;
		boolean yes = params[0] == 1;
		switch(command)
		{
			case "shooter":
				control.setSetpoint(params[1]);
				aim(yes ? Aim.UP : Aim.DOWN);
				break;
			case "aim":
				aim(yes ? Aim.UP : Aim.DOWN);
				break;
			case "flywheel":
				control.setSetpoint(params[1]);
				break;
			case "fire":
				startFire();
				break;
			case "loader":
				loader.set(yes);
				break;
		}
	}
	
	@Override
	public synchronized boolean isAutoDone()
	{
		return done;
	}
	
	@Override
	public String[] getConstantRequest()//Request all needed constants
	{
		return constants;
	}
	
	@Override
	public synchronized void returnConstantRequest(double[] constantsin)//Get all needed constants
	{
		AIMUPPOWERED = constantsin[0] == 1 ? true : false;
		SHOOTEROUTPUTRANGEHIGH = constantsin[1];
		SHOOTEROUTPUTRANGELOW = constantsin[2];
		SHOOTERIGAIN = constantsin[3];
		for(int i = 4; i < constantsin.length; i++)//get all the flydirs
		{
			FLYDIRS[i - 4] = constantsin[i];
		}
		
		control = new TakeBackHalf(new double[]{SHOOTEROUTPUTRANGEHIGH, SHOOTEROUTPUTRANGELOW}, SHOOTERIGAIN);
		control.setSetpoint(0);
		if (!flyThread.isAlive())
		{
			flyThread.start();
		}
	}

	@Override
	public synchronized void update()//Flywheel control loop
	{
		//Take back half control
		double output = 0;
		if(control == null)
		{
			throw new NullPointerException("No TakeBackHalf controller in Subsystem \"" + this +  "\" - constants not returned");
		}
		else
		{
			double process = tach.getRate()*RPMTORPS;//Get rps > to rpm
			output = control.output(process);
		}
		
		fly1.set(output*FLYDIRS[0]);
		fly2.set(output*FLYDIRS[1]);
		fly3.set(output*FLYDIRS[2]);
		fly4.set(output*FLYDIRS[3]);
		
		//Shooter update
		if(firing && !firingLast)//Starting firing sequence
		{
			shootingTimer.reset();
			shootingTimer.start();
			loader(Load.OUT);
		}
		else if(firing && !firingLast)//Update firing sequence
		{
			if(shootingTimer.get() > GRABFRISBEETIME)
		    {
		    	loader(Load.IN);
		    }
		    if(shootingTimer.get() > GRABFRISBEETIME + SHOOTFRISBEETIME)
		    {
		    	shootingTimer.stop();
		    	shootingTimer.reset();
		    	firing = false;
		    }
		}
	}
	
	public synchronized void startFire()
	{
		firing = true;
	}
	
	public synchronized boolean isFiring()
	{
		return firing;
	}
	
	public synchronized void aim(Aim dir)
	{
		switch(dir)
		{
			case UP:
				aim.set(AIMUPPOWERED ? true : false);
				break;
			case DOWN:
				aim.set(AIMUPPOWERED ? false : true);
				break;
		}
	}
	
	public synchronized void loader(Load dir)
	{
		switch(dir)
		{
			case IN:
				loader.set(false);
				break;
			case OUT:
				loader.set(true);
				break;
		}
	}
	
	public String toString()
	{
		return "Shooter";
	}
}
