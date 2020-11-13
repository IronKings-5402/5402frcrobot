/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;

import com.ctre.phoenix.sensors.*;

//import org.graalvm.compiler.phases.schedule.SchedulePhase;
import com.analog.adis16470.frc.ADIS16470_IMU;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public int irbeam1tripped = 0;
  public int irbeam2tripped = 0;
  public int irbeam3tripped = 0;
  public int irbeam4tripped = 0;
  public int irbeam5tripped = 0;
  // motors

  private WPI_VictorSPX Intake3 = new WPI_VictorSPX(3);
  private WPI_VictorSPX Intake2 = new WPI_VictorSPX(2);
  private WPI_VictorSPX Intake4 = new WPI_VictorSPX(4);
  private VictorSP Intake1 = new VictorSP(2);
  private VictorSP Intake5 = new VictorSP(4);
  private WPI_TalonSRX RightShooter = new WPI_TalonSRX(1);
  private VictorSP LeftShooter = new VictorSP(5);
  private VictorSP Flag = new VictorSP(0);
  private WPI_VictorSPX Intake0 = new WPI_VictorSPX(0);
  private VictorSP Winch = new VictorSP(6);
  // Drive
  private WPI_TalonSRX FrontRight = new WPI_TalonSRX(3);
  private WPI_TalonSRX BackRight = new WPI_TalonSRX(5);
  private WPI_TalonSRX FrontLeft = new WPI_TalonSRX(0);
  private WPI_TalonSRX BackLeft = new WPI_TalonSRX(2);
  // XBOX controller
  private GenericHID controller = new Joystick(0);
  private GenericHID Xbox = new Joystick(1);
  // ir beam sensors
  private DigitalInput virtualirbeam1 = new DigitalInput(0);
  private DigitalInput virtualirbeam2 = new DigitalInput(1);
  private DigitalInput virtualirbeam3 = new DigitalInput(2);
  private DigitalInput virtualirbeam4 = new DigitalInput(3);
  private DigitalInput virtualirbeam5 = new DigitalInput(4);
  private DigitalInput Roller = new DigitalInput(5);
  private final ADIS16470_IMU Gyro = new ADIS16470_IMU();
  double kP = 1;
  int buttonpressed = 0;
  public Boolean SensorOn = false;
  
  public int irSensorcheck() {
    if (!virtualirbeam1.get()) {
      irbeam1tripped = 1;
      System.out.println("DIO 1 input detected");
      Intake1.set(0);
      return 1;
    } 
    else {

    }
    if ((!virtualirbeam2.get()) && (irbeam1tripped == 1)) {
      irbeam2tripped = 1;
      System.out.println("DIO 2 input detected");
      Intake3.set(0);
      return 2;
    } 
    else {

    }
    if ((!virtualirbeam3.get()) && (irbeam2tripped == 1)) {
      irbeam3tripped = 1;
      System.out.println("DIO 3 input detected");
      Intake5.set(0);
      return 3;
    } 
    else {

    }
    if ((!virtualirbeam4.get()) && ((irbeam3tripped == 1))) {
      irbeam4tripped = 1;
      System.out.println("DIO 4 input detected");
      Intake4.set(0);
      return 4;
    } 
    else {

    }
    if ((!virtualirbeam5.get()) && (irbeam4tripped == 1)) {
      irbeam5tripped = 1;
      System.out.println("DIO 5 input detected");
      Intake2.set(0);
      return 5;
    } 
    else {
      return 0;
    }
  }
  /**
   * 
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // Starts USB camera
    CameraServer.getInstance().startAutomaticCapture();
    irbeam1tripped = 0;
    irbeam2tripped = 0;
    irbeam3tripped = 0;
    irbeam4tripped = 0;
    irbeam5tripped = 0;

  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Gyro sensor", Gyro.getAngle());
    /// sending the data to been seen
    // SmartDashboard.putNumber("Left Drive Encoder Value",
    // LeftMaster.getSelectedSensorPosition());// * kDriveTick2Feet);
    // SmartDashboard.putNumber("Right Drive Encoder Value",
    // RightMaster.getSelectedSensorPosition());// * kDriveTick2Fee
    SmartDashboard.putBoolean("Button1", SensorOn);
  }

  @Override
  public void autonomousInit() {

    /*
     * while (Gyro.getAngle() != 0) { if (Gyro.getAngle() > 0){ FrontLeft.set(1);
     * BackLeft.set(1); FrontRight.set(-1); BackLeft.set(-1); } else if
     * (Gyro.getAngle() < 0) { FrontLeft.set(-1); BackLeft.set(-1);
     * FrontRight.set(1); BackLeft.set(1); } else { FrontLeft.set(0);
     * BackLeft.set(0); FrontRight.set(0); BackLeft.set(0); } FrontLeft.set(0);
     * BackLeft.set(0); FrontRight.set(0); BackLeft.set(0);
     * 
     * }
     */

  }

  @Override
  public void autonomousPeriodic() {

    /*
     * double error = -Gyro.getRate();
     * 
     * 
     * FrontLeft.set(.5 + kP * error); BackLeft.set(.5 + kP * error);
     * FrontRight.set(.5 - kP * error); BackRight.set(.5 - kP * error);
     * SmartDashboard.putNumber("Right Side", .5 - kP * error);
     * SmartDashboard.putNumber("Left Side", .5 + kP * error);
     * SmartDashboard.putNumber("Gyro Rate", Gyro.getRate());
     * SmartDashboard.putNumber("Gyro Angle", Gyro.getAngle());
     */

  }

  @Override
  public void teleopInit() {
    irbeam1tripped = 0;
    irbeam2tripped = 0;
    irbeam3tripped = 0;
    irbeam4tripped = 0;
    irbeam5tripped = 0;
    Intake1.set(-0.5);
    Intake2.set(-0.5);
    Intake3.set(-0.5);
    Intake4.set(-0.5);
    Intake5.set(0.5);
    LeftShooter.set(0);
    RightShooter.set(0);
    buttonpressed = 0;
    final Boolean SensorOn = false;
    // public SensorOn.booleanValue();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    SpeedControllerGroup Left = new SpeedControllerGroup(FrontLeft, BackLeft);
    SpeedControllerGroup Right = new SpeedControllerGroup(FrontRight, BackRight);
    double left = controller.getRawAxis(1) * -0.6;

    double right = -Xbox.getRawAxis(5) * 0.6;
     Intake1.set(-0.7);
     Intake2.set(-0.7);
     Intake3.set(-0.7);
     Intake4.set(-0.7);
     Intake5.set(0.7);

     if (controller.getRawButton(2)){
     Flag.set(-1);

     }
     else{
     Flag.set(0);
     }
     if (controller.getRawButton(1)){
     Winch.set(1);

     }
     else{
     Winch.set(0);
     }
     if (controller.getRawButton(3)){
     Winch.set(-1);
     }
     else {

     }

     if (controller.getRawButton(4)){
     Flag.set(1);
     }
     if (controller.getRawButton(8)) {
     buttonpressed = 1;
     }
     else {

     }
     
    if (!virtualirbeam1.get()) {
      irbeam1tripped = 1;
      System.out.println("DIO 1 input detected");
      Intake1.set(0);
    } 
    else {

    }
    if ((!virtualirbeam2.get()) && (irbeam1tripped == 1)) {
      irbeam2tripped = 1;
      System.out.println("DIO 2 input detected");
      Intake3.set(0);
    } 
    else {

    }
    if ((!virtualirbeam3.get()) && (irbeam2tripped == 1)) {
      irbeam3tripped = 1;
      System.out.println("DIO 3 input detected");
      Intake5.set(0);
    } 
    else {

    }
    if ((!virtualirbeam4.get()) && ((irbeam3tripped == 1))) {
      irbeam4tripped = 1;
      System.out.println("DIO 4 input detected");
      Intake4.set(0);
    } 
    else {

    }
    if ((!virtualirbeam5.get()) && (irbeam4tripped == 1)) {
      irbeam5tripped = 1;
      System.out.println("DIO 5 input detected");
      Intake2.set(0);
    } 
    else {

    }
  }

  // }
  /*
   * else { Intake0.set(1); Intake1.set(1); Intake2.set(1); Intake3.set(1);
   * Intake4.set(1); Intake5.set(1); RightShooter.set(1); LeftShooter.set(1);
   * buttonpressed = 0; Intake0.set(0); Intake1.set(0); Intake2.set(0);
   * Intake3.set(0); Intake4.set(0); Intake5.set(0); RightShooter.set(0);
   * LeftShooter.set(0); }
   * 
   * 
   * 
   * /*if (controller.getRawButtonPressed(5)){ Intake1.set(0); Intake2.set(0);
   * Intake3.set(0); Intake4.set(0); Intake5.set(0); irbeam1tripped = 0;
   * irbeam2tripped = 0; irbeam3tripped = 0; irbeam4tripped = 0; irbeam5tripped =
   * 0;
   * 
   * }
   */

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
    

  }
  
}

