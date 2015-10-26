
package org.usfirst.frc.team3476.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team3476.Utility.*;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay.Value;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	Joystick xbox = new Joystick(0);
	Joystick joystick = new Joystick(1);
	
	double xAxis = -xbox.getRawAxis(4);
	double yAxis = -xbox.getRawAxis(1);
	double rightTrigger = xbox.getRawAxis(3);
	
	Talon flyTalon1 = new Talon(0);
	Talon flyTalon2 = new Talon(1);
	Talon flyTalon3 = new Talon(2);
	Talon flyTalon4 = new Talon(3);
	//Flywheel constants
	final double FLY1 = -1, FLY2 = 1, FLY3 = -1, FLY4 = 1; 
	//Fly1 and Fly3 are bottom fly motors. Fly2 and Fly4 are top motors.
	
	Talon dropIntakeMotor = new Talon(9); 
	Talon mainIntakeMotor = new Talon(6);
	RobotDrive drive = new RobotDrive(7, 8, 4, 5);
	Solenoid aimSolenoid = new Solenoid(1);
	Solenoid loadSolenoid = new Solenoid(2);
	Solenoid grappleSolenoid = new Solenoid(0);
	Solenoid shifterSoleniod = new Solenoid(3);
	Timer loadTimer = new Timer();
	Relay dropdown = new Relay(2);
	
	//Shooter timer boolean
	boolean runningTimer = false;
	
	//Grapple toggle
	Toggle grapple = new Toggle();
	
	enum Mode {DEFAULT, INTAKE, SHOOTUP, SHOOTDOWN}
    Mode mode = Mode.DEFAULT;
    
    //Shifting state
    RunningAverage avgRate = new RunningAverage(8);
    final double IPS = 48.0;
    final double HYSTERESIS = 0.2;
    enum ShiftingState {LOW, HIGH}
    ShiftingState shiftingState = ShiftingState.LOW;
    
    //Motor constants
	double SUCKMOTORSPEED = -1.0;
	double LOADMOTORSPEED = -1.0;
	double GRABFRISBEETIME = 0.65;
	double SHOOTFRISBEETIME = 0.33;
    
    //Joystick buttons
    final int DEFAULT = 12, INTAKE = 7, HIGH = 11, LOW = 9, TRIGGER = 1, MANUALFIRE = 2, GRAPPLE = 5, REVERSE = 3;//todo get button numbers for "-1"'s
    boolean defaultButton = joystick.getRawButton(DEFAULT);
    boolean intakeButton = joystick.getRawButton(INTAKE);
    boolean highButton = joystick.getRawButton(HIGH);
    boolean lowButton = joystick.getRawButton(LOW);
    boolean trigger = joystick.getRawButton(TRIGGER);
    boolean manualFireButton = joystick.getRawButton(MANUALFIRE);
    boolean grappleButton = joystick.getRawButton(GRAPPLE);
    boolean reverseButton = joystick.getRawButton(REVERSE);
    
    //Xbox buttons
    final int INTAKEUP = 5, INTAKEDOWN = 6;
    boolean intakeUpButton = xbox.getRawButton(INTAKEUP);
    boolean intakeDownButton = xbox.getRawButton(INTAKEDOWN);
    
    //Encoders
    Encoder leftDrive = new Encoder(3, 4, true, EncodingType.k4X);
    Encoder rightDrive = new Encoder(1, 2, true, EncodingType.k4X);

	public void robotInit()
	{
    	loadTimer.start();
    	
    	System.out.println("load timer: " + loadTimer.get());
    	leftDrive.setDistancePerPulse(0.01225688428613428232514076911301);
    	rightDrive.setDistancePerPulse(0.01225688428613428232514076911301);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {}

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic()
    {
    	avgRate.addValue((rightDrive.getRate() + leftDrive.getRate())/2);
    	//System.out.println("Right: " + rightDrive.getDistance() + ", Left: " + leftDrive.getDistance() + ", Average Rate: " + (rightDrive.getRate() + leftDrive.getRate())/2);
    	xAxis = -xbox.getRawAxis(4);
    	yAxis = -xbox.getRawAxis(1);
    	rightTrigger = xbox.getRawAxis(3);
    	
//    	leftTalon1.set(xAxis+yAxis);
//    	rightTalon1.set(yAxis-xAxis);
//    	leftTalon2.set(xAxis+yAxis);
//    	rightTalon2.set(yAxis-xAxis);
    
    	
    	//may have to reverse motors for shooter
    	//buttons and values may have to be changed
    	System.out.println(xAxis);
    	drive.arcadeDrive(yAxis, xAxis + (.075));
    	//Poll joystick buttons
    	defaultButton = joystick.getRawButton(DEFAULT);
        intakeButton = joystick.getRawButton(INTAKE);
        highButton = joystick.getRawButton(HIGH);
        lowButton = joystick.getRawButton(LOW);
        trigger = joystick.getRawButton(TRIGGER);
        manualFireButton = joystick.getRawButton(MANUALFIRE);
        grappleButton = joystick.getRawButton(GRAPPLE);
        reverseButton = joystick.getRawButton(REVERSE);
    	
    	//Poll xbox buttons
        intakeUpButton = xbox.getRawButton(INTAKEUP);
        intakeDownButton = xbox.getRawButton(INTAKEDOWN);
    	
    	//enum set mode block
    	if(defaultButton)
    	{
    		mode = Mode.DEFAULT;
    	}
    	else if(highButton)
    	{
    		mode = Mode.SHOOTUP;
    	}
    	else if(lowButton)
    	{
    		mode = Mode.SHOOTDOWN;
    	}
    	else if(intakeButton)
    	{
    		mode = Mode.INTAKE;
    	}
    	
    	//mode switch block
    	switch(mode)
    	{
    		case INTAKE:
    			aimSolenoid.set(false);
        		flyTalon1.set(0);
    	    	flyTalon2.set(0);
    	    	flyTalon3.set(0);
    	    	flyTalon4.set(0);
    	    	dropIntakeMotor.set(reverseButton ? -SUCKMOTORSPEED : SUCKMOTORSPEED);
    	    	mainIntakeMotor.set(reverseButton ? -LOADMOTORSPEED : LOADMOTORSPEED);
    	    	//tells if the mode is activated
    	    	//the mode is working properly it's just that the both motors starts when the robot starts
    	    	//intake motor is spinning in the wrong direction
    	    	break;
    	    	
    		case SHOOTDOWN:
    			aimSolenoid.set(false);
        		flyTalon1.set(FLY1);
    	    	flyTalon2.set(FLY2);    	
    	    	flyTalon3.set(FLY3);    	
    	    	flyTalon4.set(FLY4);
    	    	dropIntakeMotor.set(0);
    	    	mainIntakeMotor.set(0);
    	    	break;
    	    	
    		case SHOOTUP:
    			aimSolenoid.set(true);
        		flyTalon1.set(FLY1);
    	    	flyTalon2.set(FLY2);    	
    	    	flyTalon3.set(FLY3);    	
    	    	flyTalon4.set(FLY4);
    	    	dropIntakeMotor.set(0);
    	    	mainIntakeMotor.set(0);
    	    	break;
    	    	
    	    default: //is the default case and also the DEFAULT mode case
    	    	aimSolenoid.set(false);
        		flyTalon1.set(0);
    	    	flyTalon2.set(0);    	
    	    	flyTalon3.set(0);    	
    	    	flyTalon4.set(0);
    	    	dropIntakeMotor.set(0);
    	    	mainIntakeMotor.set(0);
    	    	break;
    	    	
    	}
    	
    	//old mode switching if above doesn't work
    	/*if (mode == Mode.DEFAULT)
    	{
    		aimSolenoid.set(false);
    		flyTalon1.set(0);
	    	flyTalon2.set(0);    	
	    	flyTalon3.set(0);    	
	    	flyTalon4.set(0);
	    	dropIntakeMotor.set(0);
	    	mainIntakeMotor.set(0);
    	}
    	else if (mode == Mode.INTAKE)
    	{
    		aimSolenoid.set(false);
    		flyTalon1.set(0);
	    	flyTalon2.set(0);    	
	    	flyTalon3.set(0);    	
	    	flyTalon4.set(0);
	    	dropIntakeMotor.set(SUCKMOTORSPEED);
	    	mainIntakeMotor.set(LOADMOTORSPEED);
    	}
    	else if (mode == Mode.SHOOTDOWN)
    	{
    		aimSolenoid.set(false);
    		flyTalon1.set(1);
	    	flyTalon2.set(1);    	
	    	flyTalon3.set(1);    	
	    	flyTalon4.set(1);
	    	dropIntakeMotor.set(0);
	    	mainIntakeMotor.set(0);
    	}
    	else if (mode == Mode.SHOOTUP)
    	{
    		aimSolenoid.set(true);
    		flyTalon1.set(1);
	    	flyTalon2.set(1);    	
	    	flyTalon3.set(1);    	
	    	flyTalon4.set(1);
	    	dropIntakeMotor.set(0);
	    	mainIntakeMotor.set(0);
    	}*/
	    
	    //Frisbee auto-loading sequence
	    if (trigger && !runningTimer)
	    {
	    	loadTimer.start();
	    	runningTimer = true;
			loadSolenoid.set(true);
	    }
	    if(runningTimer && loadTimer.get() > GRABFRISBEETIME)
	    {
	    	loadSolenoid.set(false);
	    }
	    if(runningTimer && loadTimer.get() > GRABFRISBEETIME + SHOOTFRISBEETIME)
	    {
	    	loadTimer.stop();
	    	loadTimer.reset();
	    	runningTimer = false;
	    }
	    
	    //Manual fire
	    if(!runningTimer) //If not auto-loading, manual
	    {
	    	loadSolenoid.set(manualFireButton);
	    }
	    
	    //Manual shifting
	    if(rightTrigger >= 0.5)//shift high
	    {
	    	shifterSoleniod.set(false);
	    }
	    else//auto shifting
	    {
	    	switch(shiftingState)
	    	{
	    		case HIGH:
	    			System.out.print("Case: HIGH, Rate = " + Math.abs(avgRate.getAverage()));
	    			if(Math.abs(avgRate.getAverage()) <= IPS - HYSTERESIS) shiftingState = ShiftingState.LOW;
	    			shifterSoleniod.set(false);
	    			break;
	    		case LOW:
	    			System.out.print("Case: LOW, Rate = " + Math.abs(avgRate.getAverage()));
	    			if(Math.abs(avgRate.getAverage()) >= IPS + HYSTERESIS) shiftingState = ShiftingState.HIGH;
	    			shifterSoleniod.set(true);
	    			break;
	    	}
	    }
	    System.out.println(", Instant Rate = " + (rightDrive.getRate() + leftDrive.getRate())/2);
	    
	    //Grapple toggle
	    //grapple is of type Toggle
	    grapple.input(grappleButton);
	    grappleSolenoid.set(grapple.get());
	    
	    
	    //TODO: Check if this is right (Forward, backward, etc.)
	    if((intakeUpButton && intakeDownButton) || (!intakeUpButton && !intakeDownButton)) //Both or none are pressed
	    {
	    	dropdown.set(Relay.Value.kOff);
	    	//System.out.println("both buttons are being pressed?: " + dropdown.get());
	    }
	    else if(intakeUpButton) //Up is pressed
	    {
	    	dropdown.set(Relay.Value.kForward);
	    	//System.out.println("Up button pressed?: " + dropdown.get());
	    }
	    else //Only remaining option is down is pressed
	    {
	    	dropdown.set(Relay.Value.kReverse);
	    	//System.out.println("Down button pressed?: " + dropdown.get());
	    }
	 }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {}
}