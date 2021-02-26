/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  NetworkTableInstance inst = NetworkTableInstance.getDefault();
  NetworkTable table = inst.getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry tv = table.getEntry("tv");
  NetworkTableEntry pipeline = table.getEntry("pipeline");
  
  // Talon FX controller
  private TalonFX LeftFrontWheel = new TalonFX(3);
  private TalonFX LeftBackWheel = new TalonFX(0);
  private TalonFX RightFrontWheel = new TalonFX(1);
  private TalonFX RightBackWheel = new TalonFX(2);
  // xbox controller
  private GenericHID Xbox = new Joystick(0);
  // no speedControllerGroups so I made my own function 
  public void setMotors(double left, double right) {
    LeftBackWheel.set(ControlMode.PercentOutput, left);
    LeftFrontWheel.set(ControlMode.PercentOutput, left);
    RightBackWheel.set(ControlMode.PercentOutput, right);
    RightFrontWheel.set(ControlMode.PercentOutput, right);
  }
  // deadband function. Takes Controller Value in doouble data type and deadband ammount in double datatype as values
  public double applydeadband(double ControllerValue, double DeadBandAmmount) {
    // if controller value is greater than or equal to negative dead band ammount 
    //and controllerValue is less than or equal to deadband ammount then return 0
    if (ControllerValue >= -DeadBandAmmount && ControllerValue <= DeadBandAmmount) {
      return 0;
    }
    // else return contoller value
    else { 
      return ControllerValue;
    }
  }
  // Mecanum function.  Takes string as value
  public void Mecanum(String direction){
    // if direction is left mecanum left
    if (direction == "left"){
      LeftFrontWheel.set(ControlMode.PercentOutput, -.25);
      LeftBackWheel.set(ControlMode.PercentOutput, .25);
      RightFrontWheel.set(ControlMode.PercentOutput, -.25);
      RightBackWheel.set(ControlMode.PercentOutput, .25);
    }
    // if direction is right mecanum right
    else if (direction == "right"){
      LeftFrontWheel.set(ControlMode.PercentOutput, .25);
      LeftBackWheel.set(ControlMode.PercentOutput, -.25);
      RightFrontWheel.set(ControlMode.PercentOutput, .25);
      RightBackWheel.set(ControlMode.PercentOutput, -.25);
    }
  }
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    // Calls set function. Sets all motors to 0]
    // stops motors
    setMotors(0, 0);
    pipeline.setNumber(1);
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    pipeline.setNumber(1);

    
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    double x = tx.getDouble(0.0);
    System.out.println("X Value: " + String.valueOf(x));
    double area = ta.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double target = tv.getDouble(0.0);
    System.out.println("Percent of image: " + String.valueOf(area));
    double distance = (9.5-17.51)/(Math.tan(Math.toRadians(179)+ Math.toRadians(y)));
    System.out.println("Distance: " + String.valueOf(distance));
    
    if (target == 1 & area < 20) {
      if (x > 2.5){
        setMotors(x*.04, x*.05/3.5);
      }
      else if (x < -2.5){
        setMotors(Math.abs(x)*.05/3.5, x*.04);
      }
      else if (x >= -2.5 & x <= 2.5){
        setMotors(.25, -.25);
      }
    }
    else {
      setMotors(0, 0);
    }
  }
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopInit() {
    
  }
  @Override
  public void teleopPeriodic() {
    // Gets values from xbox controller
    // Stick values equal apply deadband with Controller axis for first argument and deadband ammount for second argument. 
    double LeftStick = applydeadband(-Xbox.getRawAxis(1), 0.07);
    double RightStick = applydeadband(Xbox.getRawAxis(5), 0.07);
    // if left dpad is pressed do mecaum function with left as argument
    if (Xbox.getPOV() == 270) {
      Mecanum("left");
    }
    // if right dpad is pressed Do mecnum function with right as value
    else if (Xbox.getPOV() == 90) {
      Mecanum("right");
    }
    // else set motors to stick values
    else {
      setMotors(LeftStick, RightStick);
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    // testing area
    System.out.println(Xbox.getRawAxis(5));
  }
}
