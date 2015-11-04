	package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;
import org.usfirst.frc.team3476.Utility.RunningAverage;
import org.usfirst.frc.team3476.Utility.Control.BangBang;
import org.usfirst.frc.team3476.Utility.Control.DifferentialGyro;
import org.usfirst.frc.team3476.Utility.Control.PIDOutputWrapper;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;

//TODO: Synchronize relevant methods > thread safe
public class Drive implements Subsystem
{
	private final String[] autoCommands = {"turn", "drive", "driven", "shiftit", "clear"};
	private final String[] constants = {"DRIVEDEAD", "DRIVESTRAIGHTDEAD", "TURNDEAD", "USELEFT", "USERIGHT", "STRAIGHTP", "STRAIGHTI", "STRAIGHTD", "DRIVEP", "DRIVEI", "DRIVED", "TURNP", "TURNI", "TURND", "SHIFTINGSPEED", "SHIFTINGHYS"};
	final int ENCODERSAMPLES = 16;
	
	private boolean done, driveStraight, simple, autoShifting, USELEFT, USERIGHT, clear;
	private double DRIVEDEAD, DRIVESTRAIGHTDEAD, TURNDEAD, DRIVEP, DRIVEI, DRIVED, TURNP, TURNI, TURND, STRAIGHTP, STRAIGHTI, STRAIGHTD, SHIFTINGSPEED, SHIFTINGHYS;
	
	private Encoder left, right;
	private DifferentialGyro gyro;
	private RunningAverage encoderAvg, avgRate;
	private RobotDrive driveTrain;
	private Solenoid shifters;
	
	private PIDController drive, turn, straightTurn;
	private PIDOutputWrapper driveWrapper, turnWrapper, straightWrapper;
	private BangBang driven;
	
	private SubsystemTask task;
	private Thread driveThread;
	
	public enum ShiftingState {LOW, HIGH}
	ShiftingState shiftingState;
	
	public Drive(Encoder leftin, Encoder rightin, DifferentialGyro gyroin, RobotDrive driveTrainin, Solenoid shiftersin)
	{
		done = true;
		driveStraight = true;
		simple = false;
		clear = true;
		autoShifting = true;
		shiftingState = ShiftingState.LOW;
		
		left = leftin;
		right = rightin;
		gyro = gyroin;
		driveTrain = driveTrainin;
		shifters = shiftersin;
		
		encoderAvg = new RunningAverage(ENCODERSAMPLES);
		avgRate = new RunningAverage(ENCODERSAMPLES);
		driveWrapper = new PIDOutputWrapper();
		turnWrapper = new PIDOutputWrapper();
		straightWrapper = new PIDOutputWrapper();
		
		drive = new PIDController(DRIVEP, DRIVEI, DRIVED, encoderAvg, driveWrapper);
		turn = new PIDController(TURNP, TURNI, TURND, gyro, turnWrapper);
		straightTurn = new PIDController(STRAIGHTP, STRAIGHTI, STRAIGHTD, gyro, straightWrapper);
		driven = new BangBang(new double[]{1, -1});
		
		task = new SubsystemTask(this);
		driveThread = new Thread(task, "driveThread");
		driveThread.start();
	}
	
	@Override
	public String[] getAutoCommands()
	{
		return autoCommands;
	}

	@Override
	public synchronized void doAuto(double[] params, String command)
	{
		autoShifting = false;
		done = false;
		clear = false;
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
			case "shiftit":
				setShifterState(params[0] == 1 ? ShiftingState.HIGH : ShiftingState.LOW);
				done = true;
				break;
			case "clear":
				clear = true;
				done = true;
				break;
		}
	}

	@Override
	public synchronized boolean isAutoDone()
	{
		return done;
	}

	@Override
	public String[] getConstantRequest()
	{
		return constants;
	}

	@Override
	public synchronized void returnConstantRequest(double[] constantsin)
	{
		
		int i = 0;
		DRIVEDEAD = constantsin[i];
		i++;//1
		DRIVESTRAIGHTDEAD = constantsin[i];
		i++;//2
		TURNDEAD = constantsin[i];
		i++;//3
		USELEFT = constantsin[i] == 1;
		i++;//4
		USERIGHT = constantsin[i] == 1;
		i++;//5
		STRAIGHTP = constantsin[i];
		i++;//6
		STRAIGHTI = constantsin[i];
		i++;//7
		STRAIGHTD = constantsin[i];
		i++;//8
		DRIVEP = constantsin[i];
		i++;//9
		DRIVEI = constantsin[i];
		i++;//10
		DRIVED = constantsin[i];
		i++;//11
		TURNP = constantsin[i];
		i++;//12
		TURNI = constantsin[i];
		i++;//13
		TURND = constantsin[i];
		i++;//14
		SHIFTINGSPEED = constantsin[i];
		i++;//15
		SHIFTINGHYS = constantsin[i];
		
		startThreads();
	}

	@Override
	public synchronized void update()
	{
		//Poll the encoders - see what up
		pollEncoders();
		if(!clear)
		{
			if (!done)
			{
				if (driveStraight)
				{
					if (!simple)
					{
						driveTrain.arcadeDrive(driveWrapper.getOutput(), straightWrapper.getOutput());
					}
					else
					{
						driveTrain.arcadeDrive(driven.output(getAvgEncoder()), straightWrapper.getOutput());
						System.out.println("Drive setpoint: " + driven.getSetpoint() + " Current pos: " + getAvgEncoder());
					}
				}
				else//Turning or something
				{
					driveTrain.arcadeDrive(0, turnWrapper.getOutput());
				}
				
				//Check if we're done here 
				//TODO: Decide if the drive needs to be in the deadzone for multiple iterations
				boolean driveDone = Math.abs(drive.getSetpoint() - encoderAvg.pidGet()) < DRIVEDEAD;
				boolean drivenDone = Math.abs(driven.getSetpoint() - getAvgEncoder()) < DRIVEDEAD;
				boolean turnDone = Math.abs(turn.getSetpoint() - encoderAvg.pidGet()) < TURNDEAD;
				boolean straightDone = Math.abs(straightTurn.getSetpoint() - encoderAvg.pidGet()) < DRIVESTRAIGHTDEAD;
				done = driveStraight ? ((simple ? drivenDone : driveDone) && straightDone) : turnDone;
			}
			else
			{
				driveTrain.arcadeDrive(0, 0);
			}
		}
		else
		{
			driveTrain.arcadeDrive(0, 0);
		}
		
		if(autoShifting)
		{
			switch(shiftingState)
	    	{
	    		case HIGH:
	    			//System.out.print("Case: HIGH, Rate = " + Math.abs(avgRate.getAverage()));
	    			if(Math.abs(avgRate.getAverage()) <= SHIFTINGSPEED - SHIFTINGHYS)
	    			{
	    				shiftingState = ShiftingState.LOW;
	    				setShifterState(shiftingState);
	    			}
	    			break;
	    		case LOW:
	    			//System.out.print("Case: LOW, Rate = " + Math.abs(avgRate.getAverage()));
	    			if(Math.abs(avgRate.getAverage()) >= SHIFTINGSPEED + SHIFTINGHYS)
    				{
	    				shiftingState = ShiftingState.HIGH;
	    				setShifterState(shiftingState);
    				}
	    			break;
	    	}
		}
	}
	
	public synchronized void executeTurn(double delta)
	{
		simple = false;
		driveStraight = false;
		drive.setSetpoint(getAvgEncoder());
		gyro.reset();
		straightTurn.setSetpoint(delta);
	}
	
	public synchronized void executeDrive(double delta)
	{
		simple = false;
		driveStraight = true;
		drive.setSetpoint(getAvgEncoder() + delta);
		gyro.reset();
		straightTurn.setSetpoint(0);
	}
	
	public synchronized void executeSimpleDrive(double delta, double speed)
	{
		simple = true;
		driveStraight = true;
		driven = new BangBang(new double[]{speed/100, -speed/100});
		driven.setSetpoint(getAvgEncoder() + delta);
		gyro.reset();
		straightTurn.setSetpoint(0);
	}
	
	public synchronized double getAvgEncoder()
	{
		return encoderAvg.getAverage();
	}
	
	public synchronized void pollEncoders()
	{
		if(USELEFT && USERIGHT)
		{
			encoderAvg.addValue((left.getDistance() + right.getDistance())/2);
			avgRate.addValue((left.getRate() + right.getRate())/2);
		}
		else if(USELEFT)
		{
			encoderAvg.addValue(left.getDistance());
			avgRate.addValue(left.getRate());
		}
		else if(USERIGHT)
		{
			encoderAvg.addValue(right.getDistance());
			avgRate.addValue(right.getRate());
		}
	}
	
	public String toString()
	{
		return "Drive";
	}
	
	public void stopThreads()
	{
		task.hold();
	}
	
	public void terminateThreads()
	{
		task.terminate();
		try
		{
			driveThread.join();
			System.out.println("Ended " + this + " thread.");
		}
		catch(InterruptedException e)
		{
			System.out.println("Ended " + this + " thread.");
		}
	}
	
	public void autoShifting(boolean auto)
	{
		autoShifting = auto;
	}
	
	public synchronized void setShifterState(ShiftingState state)
	{
		switch(state)
		{
			case HIGH:
				shifters.set(false);
				break;
			case LOW:
				shifters.set(true);
				break;
		}
	}
	
	@Override
	public void startThreads()
	{
		task.resume();
	}
}
