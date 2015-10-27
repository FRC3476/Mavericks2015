package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;
import org.usfirst.frc.team3476.Utility.RunningAverage;
import org.usfirst.frc.team3476.Utility.Control.BangBang;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SpeedController;

//TODO: Synchronize relevant methods > thread safe
public class Drive implements Subsystem
{
	private final String[] autoCommands = {"shooter", "aim", "flywheel"};
	private final String[] constants = {"DRIVESTRAIGHTDEAD", "TURNDEAD", "USELEFT", "USERIGHT"};
	final int ENCODERSAMPLES = 16;
	
	private boolean done, driveStraight, simple, USELEFT, USERIGHT;
	private double DRIVESTRAIGHTDEAD, TURNDEAD, DRIVEP, DRIVEI, DRIVED, TURNP, TURNI, TURND;
	
	private Encoder left, right;
	private Gyro gyro;
	private RunningAverage encoderAvg;
	private SpeedController drive1, drive2, drive3, drive4;
	
	private PIDController drive;
	private PIDController turn;
	private BangBang driven;
	
	public Drive(Encoder leftin, Encoder rightin, Gyro gyroin)
	{
		done = false;
		driveStraight = true;
		simple = false;
		
		left = leftin;
		right = rightin;
		gyro = gyroin;
		
		encoderAvg = new RunningAverage(ENCODERSAMPLES);
		
		drive = new PIDController(DRIVEP, DRIVEI, DRIVED, encoderAvg, output)
		turnSource = 
		turnOutput =  
		turn = 
		driven = 
	}
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}

	@Override
	public void doAuto(double[] params, String command)
	{
		switch(command)
		{
			case "turn":
				executeTurn(params[0]);
				break;
			case "drive":
				executeDrive(params[0]);
				break;
			case "driven":
				executeSimpleDrive(params[0], params[1]);
				break;
		}
	}

	@Override
	public boolean isAutoDone()
	{
		return done;
	}

	@Override
	public String[] getConstantRequest()
	{
		return constants;
	}

	@Override
	public void returnConstantRequest(double[] constantsin)
	{
		int i = 0;
		DRIVESTRAIGHTDEAD = constantsin[i];
		i++;//1
		TURNDEAD = constantsin[i];
		i++;//2
		USELEFT = constantsin[i] == 1;
		i++;//3
		USERIGHT = constantsin[i] == 1;
	}

	@Override
	public void update()
	{
		//Poll the encoders - see what up
		pollEncoders();
		
		double encAvg = getAvgEncoder();
		if(driveStraight)
		{
			
		}
		else//Turning or something
		{
			
		}
	}
	
	public void executeTurn(double delta)
	{
		simple = false;
		driveStraight = false;
		
	}
	
	public void executeDrive(double delta)
	{
		simple = false;
		driveStraight = true;
		
	}
	
	public void executeSimpleDrive(double delta, double speed)
	{
		simple = true;
		driveStraight = true;
		
	}
	
	public double getAvgEncoder()
	{
		return encoderAvg.getAverage();
	}
	
	public void pollEncoders()
	{
		if(USELEFT && USERIGHT)
		{
			encoderAvg.addValue((left.getDistance() + right.getDistance())/2);
		}
		else if(USELEFT)
		{
			encoderAvg.addValue(left.getDistance());
		}
		else if(USERIGHT)
		{
			encoderAvg.addValue(right.getDistance());
		}
	}
}
