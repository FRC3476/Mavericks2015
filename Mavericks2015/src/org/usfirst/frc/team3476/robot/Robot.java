
package org.usfirst.frc.team3476.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team3476.Utility.*;

import edu.wpi.first.wpilibj.DigitalOutput;
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
	
	Talon flyTalon1 = new Talon(1);
	Talon flyTalon2 = new Talon(2);
	Talon flyTalon3 = new Talon(3);
	Talon flyTalon4 = new Talon(4);
	Talon dropIntakeMotor = new Talon(5); 
	Talon mainIntakeMotor = new Talon(6);
	RobotDrive drive = new RobotDrive(1, 2, 3, 4);
	
	Solenoid aimSolenoid = new Solenoid(1);
	Solenoid loadSolenoid = new Solenoid(2);
	Solenoid grappleSolenoid = new Solenoid(3);
	Timer loadTimer = new Timer();
	Relay dropdown = new Relay(0);
	
	//Shooter timer boolean
	boolean runningTimer = false;
	
	//Grapple toggle
	Toggle grapple = new Toggle();
	
	enum Mode {DEFAULT, INTAKE, SHOOTUP, SHOOTDOWN}
    Mode mode = Mode.DEFAULT;
    
    //Joystick buttons
    final int DEFAULT = 11, INTAKE = 6, HIGH = 10, LOW = 8, TRIGGER = 0, MANUALFIRE = -1, GRAPPLE = -1;//todo get button numbers for "-1"'s
    boolean defaultButton = joystick.getRawButton(DEFAULT);
    boolean intakeButton = joystick.getRawButton(INTAKE);
    boolean highButton = joystick.getRawButton(HIGH);
    boolean lowButton = joystick.getRawButton(LOW);
    boolean trigger = joystick.getRawButton(TRIGGER);
    boolean manualFireButton = joystick.getRawButton(MANUALFIRE);
    boolean grappleButton = joystick.getRawButton(GRAPPLE);
    
    //Xbox buttons
    final int INTAKEUP = 11, INTAKEDOWN = 6;
    boolean intakeUpButton = joystick.getRawButton(INTAKEUP);
    boolean intakeDownButton = xbox.getRawButton(INTAKEDOWN);

	public void robotInit()
	{
    	loadTimer.start();
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
    	double xAxis = xbox.getRawAxis(0);
    	double yAxis = xbox.getRawAxis(1);
    	double SUCKMOTORSPEED = 1.0;
    	double LOADMOTORSPEED = 1.0;
    	double GRABFRISBEETIME = 0.33;
    	double SHOOTFRISBEETIME = 0.33;
    	
    	
    	
//    	leftTalon1.set(xAxis+yAxis);
//    	rightTalon1.set(yAxis-xAxis);
//    	leftTalon2.set(xAxis+yAxis);
//    	rightTalon2.set(yAxis-xAxis);
    
    	
    	//may have to reverse motors for shooter
    	//buttons and values may have to be changed
    	drive.arcadeDrive(yAxis, xAxis);
    	
    	//Poll joystick buttons
    	defaultButton = joystick.getRawButton(DEFAULT);
        intakeButton = joystick.getRawButton(INTAKE);
        highButton = joystick.getRawButton(HIGH);
        lowButton = joystick.getRawButton(LOW);
        trigger = joystick.getRawButton(TRIGGER);
        manualFireButton = joystick.getRawButton(MANUALFIRE);
        grappleButton = joystick.getRawButton(GRAPPLE);
    	
    	//Poll xbox buttons
        intakeUpButton = joystick.getRawButton(INTAKEUP);
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
    	    	dropIntakeMotor.set(SUCKMOTORSPEED);
    	    	mainIntakeMotor.set(LOADMOTORSPEED);
    	    	break;
    	    	
    		case SHOOTDOWN:
    			aimSolenoid.set(false);
        		flyTalon1.set(1);
    	    	flyTalon2.set(1);    	
    	    	flyTalon3.set(1);    	
    	    	flyTalon4.set(1);
    	    	dropIntakeMotor.set(0);
    	    	mainIntakeMotor.set(0);
    	    	break;
    	    	
    		case SHOOTUP:
    			aimSolenoid.set(true);
        		flyTalon1.set(1);
    	    	flyTalon2.set(1);    	
    	    	flyTalon3.set(1);    	
    	    	flyTalon4.set(1);
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
	    
	    if(!runningTimer) //If not auto-loading, manual
	    {
	    	loadSolenoid.set(manualFireButton);
	    }
	    
	    //Grapple toggle
	    //grapple is of type Toggle
	    grapple.input(grappleButton);
	    grappleSolenoid.set(grapple.get());
	    
	    //TODO: Check if this is right (Forward, backward, etc.)
	    if((intakeUpButton && intakeDownButton) || (!intakeUpButton && !intakeDownButton)) //Both or none are pressed
	    {
	    	dropdown.set(Value.kOff);
	    }
	    if(intakeUpButton) //Up is pressed
	    {
	    	dropdown.set(Value.kForward);
	    }
	    else //Only remaining option is down is pressed
	    {
	    	dropdown.set(Value.kReverse);
	    }
	 }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {}
}