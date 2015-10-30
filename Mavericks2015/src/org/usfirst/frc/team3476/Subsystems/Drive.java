package org.usfirst.frc.team3476.Subsystems;

import org.usfirst.frc.team3476.Main.Subsystem;
import org.usfirst.frc.team3476.Utility.RunningAverage;
import org.usfirst.frc.team3476.Utility.Control.BangBang;
import org.usfirst.frc.team3476.Utility.Control.DifferentialGyro;
import org.usfirst.frc.team3476.Utility.Control.PIDOutputWrapper;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;

//TODO: Synchronize relevant methods > thread safe
public class Drive implements Subsystem
{
	private final String[] autoCommands = {"shooter", "aim", "flywheel"};
	private final String[] constants = {"DRIVEDEAD", "DRIVESTRAIGHTDEAD", "TURNDEAD", "USELEFT", "USERIGHT", "STRAIGHTP", "STRAIGHTI", "STRAIGHTD"};
	final int ENCODERSAMPLES = 16;
	
	private boolean done, driveStraight, simple, USELEFT, USERIGHT;
	private double DRIVEDEAD, DRIVESTRAIGHTDEAD, TURNDEAD, DRIVEP, DRIVEI, DRIVED, TURNP, TURNI, TURND, STRAIGHTP, STRAIGHTI, STRAIGHTD;
	
	private Encoder left, right;
	private DifferentialGyro gyro;
	private RunningAverage encoderAvg;
	private RobotDrive driveTrain;
	
	private PIDController drive, turn, straightTurn;
	private PIDOutputWrapper driveWrapper, turnWrapper, straightWrapper;
	private BangBang driven;
	
	public Drive(Encoder leftin, Encoder rightin, DifferentialGyro gyroin, RobotDrive driveTrainin)
	{
		done = false;
		driveStraight = true;
		simple = false;
		
		left = leftin;
		right = rightin;
		gyro = gyroin;
		driveTrain = driveTrainin;
		
		encoderAvg = new RunningAverage(ENCODERSAMPLES);
		driveWrapper = new PIDOutputWrapper();
		turnWrapper = new PIDOutputWrapper();
		straightWrapper = new PIDOutputWrapper();
		
		drive = new PIDController(DRIVEP, DRIVEI, DRIVED, encoderAvg, driveWrapper);
		turn = new PIDController(TURNP, TURNI, TURND, gyro, turnWrapper);
		straightTurn = new PIDController(STRAIGHTP, STRAIGHTI, STRAIGHTD, gyro, straightWrapper);
		driven = new BangBang(new double[]{1, -1});
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
		DRIVEDEAD = constantsin[i];
		i++;//1
		DRIVESTRAIGHTDEAD = constantsin[i];
		i++;//2
		TURNDEAD = constantsin[i];
		i++;//3
		USELEFT = constantsin[i] == 1;
		i++;//4
		USERIGHT = constantsin[i] == 1;
	}

	@Override
	public void update()
	{
		//Poll the encoders - see what up
		pollEncoders();
		
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
