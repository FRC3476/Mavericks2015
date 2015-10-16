
package org.usfirst.frc.team3476.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
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
	Joystick controller = new Joystick(1);
	
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
	Relay relay = new Relay(0);
	
	boolean runningTimer = false;
	
	enum Mode {DEFAULT, INTAKE, SHOOTUP, SHOOTDOWN}
    Mode mode = Mode.DEFAULT;
	
	public void robotInit() {
    	loadTimer.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//7 9 11 12
    	double xAxis = controller.getRawAxis(0);
    	double yAxis = controller.getRawAxis(1);
    	double SUCKMOTORSPEED = 1.0;
    	double LOADMOTORSPEED = 1.0;
    	double GRABFRISBEETIME = 0.33;
    	double SHOOTFRISBEETIME = 0.33;
    	boolean grappleToggle = false;
    	boolean previousToggle = false;
//    	leftTalon1.set(xAxis+yAxis);
//    	rightTalon1.set(yAxis-xAxis);
//    	leftTalon2.set(xAxis+yAxis);
//    	rightTalon2.set(yAxis-xAxis);
    
    	
    	//may have to reverse motors for shooter
    	//buttons and values may have to be changed
    	drive.arcadeDrive(yAxis, xAxis);
    	
    	
    	
    	//enum set mode block
    	if (controller.getRawButton(12))
    	{
    		mode = Mode.DEFAULT;
    	}
    	else if (controller.getRawButton(11))
    	{
    		mode = Mode.SHOOTUP;
    	}
    	else if (controller.getRawButton(9))
    	{
    		mode = Mode.SHOOTDOWN;
    	}
    	else if (controller.getRawButton(11))
    	{
    		mode = Mode.INTAKE;
    	}
    	
    	if (mode == Mode.DEFAULT)
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
    	}
    	
    	if (controller.getRawButton(2))	//
    	{								// Is this something you want to keep? -Aleks
    		aimSolenoid.set(false);		//
    	}
	    
	    //frisbee loading sequence
	    if (controller.getRawButton(5) == true && runningTimer == false){
	    	loadTimer.start();
	    	runningTimer=true;
			loadSolenoid.set(true);
		}
	    
	    if(loadTimer.get() > GRABFRISBEETIME && runningTimer == true){
	    	loadSolenoid.set(false);
	    }
	    
	    if(loadTimer.get() > GRABFRISBEETIME+SHOOTFRISBEETIME && runningTimer == true){
	    	loadTimer.stop();
	    	loadTimer.reset();
	    }
	    
	    if(controller.getRawButton(8) == true){
	    	grappleSolenoid.set(true);	
	    }
	    
	    if(controller.getRawButton(9) == true){
	    	relay.set(Value.kForward);
	    } else if(controller.getRawButton(9) == true){
	    	relay.set(Value.kReverse);
	    }else{
	    	relay.set(Value.kOff);
	    }
	 }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
