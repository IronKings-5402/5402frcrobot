/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.analog.adis16470.frc.ADIS16470_IMU;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
 // private static final String kDefaultAuto = "Default";
 // private static final String kCustomAuto = "My Auto";
  
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  public int irbeam1tripped = 0;
  public int irbeam2tripped = 0;
  public int irbeam3tripped = 0;
  public int irbeam4tripped = 0;
  public int irbeam5tripped = 0;

  // Intake 
  private WPI_VictorSPX Intake0 = new WPI_VictorSPX(0);
  private VictorSP Intake1 = new VictorSP(2);
  private WPI_VictorSPX Intake2 = new WPI_VictorSPX(2);
  private WPI_VictorSPX Intake3 = new WPI_VictorSPX(3);
  private WPI_VictorSPX Intake4 = new WPI_VictorSPX(4);
  private VictorSP Intake5 = new VictorSP(4);
 
  //Shooters
  private WPI_TalonSRX RightShooter = new WPI_TalonSRX(1);
  private VictorSP LeftShooter = new VictorSP(5);
  //Climbing
  private WPI_TalonSRX Flag = new WPI_TalonSRX(6);
  private VictorSP Winch = new VictorSP(6);
  // Drive
  private WPI_TalonSRX RightMaster = new WPI_TalonSRX(3);
  private WPI_TalonSRX RightSlave = new WPI_TalonSRX(5);
  private WPI_TalonSRX LeftMaster = new WPI_TalonSRX(0);
  private WPI_TalonSRX LeftSlave = new WPI_TalonSRX(2);
  // XBOX controller
  private GenericHID Xbox = new Joystick(0);
  private GenericHID Controller = new Joystick(1);
  // ir beam sensors
  private DigitalInput virtualirbeam1 = new DigitalInput(0);
  private DigitalInput virtualirbeam2 = new DigitalInput(1);
  private DigitalInput virtualirbeam3 = new DigitalInput(2);
  private DigitalInput virtualirbeam4 = new DigitalInput(3);
  private DigitalInput virtualirbeam5 = new DigitalInput(4);
  private DigitalInput Roller = new DigitalInput(5);
  //Gyro
  private final ADIS16470_IMU Gyro = new ADIS16470_IMU();

  //private DifferentialDrive Drive = new DifferentialDrive(LeftMaster, RightMaster);

// AP value for turns used in the simple control loop below 
double kP_turn = 0.005; 

 // Define Auto states (What you want to do in Autonomous) here 
 public enum AutoStates {
  START_AUTO, 
  BEGIN_AUTO,
  FIRST_DRIVE,
  FIRST_TURN,
  SHOOT_BALLS,
  STOP_BALLS,
  SECOND_DRIVE,
  END_AUTO
 }
 // this is the state variable that tracks what your robot is currently doing in auto 
 public AutoStates robotState; 
 // A Variable to track when you start doing something 
 public double driveStartTime = 0.0; 
 // the angle you want to turn to in auto. 
 public double turnAngle = 90.0; 
 private double deadband(double in, double band) {
  double value = 0;
  if (in > band){
    value = in-band;
  }
  if( in <-band) {
    value = in+band;
  }
  return value / (1-band);
  }
  @Override
  public void robotInit() {
    //m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
   // m_chooser.addOption("My Auto", kCustomAuto);
   // SmartDashboard.putData("Auto choices", m_chooser);
    double startTime = Timer.getFPGATimestamp();

    // setting up inverted
  LeftMaster.setInverted(false);
  RightMaster.setInverted(true);

  // slave setup
  LeftSlave.follow(LeftMaster);
  RightSlave.follow(RightMaster);

  LeftSlave.setInverted(InvertType.FollowMaster);
  RightSlave.setInverted(InvertType.FollowMaster);

  // init Encoders
  RightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0, 10);
  LeftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,0, 10);

  LeftMaster.setSensorPhase(false);
RightMaster.setSensorPhase(true);

  // reset encoders to zero
  
 LeftMaster.setSelectedSensorPosition(0,0,10);
 RightMaster.setSelectedSensorPosition(0,0,10);

 // Set DeadBand


 
 //LeftMaster.setDeadband()

 // statin gthe camera
 CameraServer.getInstance().startAutomaticCapture();
 CameraServer.getInstance().startAutomaticCapture();
 // setting the line breaks to off
 irbeam1tripped = 0;
 irbeam2tripped = 0;
 irbeam3tripped = 0;
 irbeam4tripped = 0;
 irbeam5tripped = 0;
  }
  public Boolean SensorOn = false;
  public Boolean AutoOn = false;

  @Override
  public void robotPeriodic() {
if(AutoOn == true){


    if(SensorOn == false){ 
     
if (Controller.getRawButtonPressed(11)){
        Intake1.set(-1);
        Intake2.set(-.5);
        Intake3.set(-.5);
        Intake4.set(-.5);
        Intake5.set(.7);
}
     if (!virtualirbeam1.get()){
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
      if ((!virtualirbeam4.get()) && (irbeam3tripped == 1)) {
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
   else {

      }
     
      
     if(SensorOn == true){
      irbeam1tripped = 0;
      irbeam2tripped = 0;
      irbeam3tripped = 0;
      irbeam4tripped = 0;
      irbeam5tripped = 0;
      
       if(Controller.getRawButton(8)){

        Intake1.set(-.5);
        Intake2.set(-.5);
        Intake3.set(-.5);
        Intake4.set(-.5);
        Intake5.set(0.5);
      }
      else{
        Intake1.set(0);
        Intake2.set(0);
        Intake3.set(0);
        Intake4.set(0);
        Intake5.set(0);
      }
        
      
//            }
//            else {

//           }
        }
      }
      if(AutoOn == false)
      {

      }
    SmartDashboard.putNumber("Gyro sensor", Gyro.getAngle());
    ///sending the data to been seen 
  SmartDashboard.putNumber("Left Drive Encoder Value", LeftMaster.getSelectedSensorPosition());// * kDriveTick2Feet);
   SmartDashboard.putNumber("Right Drive Encoder Value", RightMaster.getSelectedSensorPosition());// * kDriveTick2Fee
   SmartDashboard.putBoolean("Sensor",SensorOn );
   SmartDashboard.putBoolean("AutoOn",AutoOn );



  }


  @Override
  public void autonomousInit() {
    //m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    //System.out.println("Auto selected: " + m_autoSelected);
    robotState = AutoStates.START_AUTO;
    LeftMaster.setSelectedSensorPosition(0,0,10);
    irbeam1tripped = 0;
    irbeam2tripped = 0;
    irbeam3tripped = 0;
    irbeam4tripped = 0;
    irbeam5tripped = 0;
    Gyro.reset();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // making it easier to set each side of the bot by making it so the whole left side runs on "LeftPositon" and right
    double LeftPosition = LeftMaster.getSelectedSensorPosition();
    double RightPosition = RightMaster.getSelectedSensorPosition(); 
   double Distance = (LeftPosition + RightPosition) / 2; // this one is for when both sides of the bot have encoders
    double Distance2 = (LeftPosition); // for now because only one side of the bot has an encoder
    // switch (m_autoSelected) {
    //   case kDefaultAuto:
    //   default:
      
    //   driveStartTime = Timer.getFPGATimestamp(); 
    
    //   // Drive straight for 5 seconds
    //   if (Distance2 > 20000)
    //   {
    //     // Stop the Motors 
    //     LeftMaster.set(0);
    //     RightMaster.set(0);
    //     break;
    // }
    switch(robotState)
   {
      case START_AUTO:
       // Do any Resets or initialization here
        // record the current time
        driveStartTime = Timer.getFPGATimestamp(); 
        // Advance to the next state
        robotState = AutoStates.BEGIN_AUTO; 
        break; 
      case BEGIN_AUTO:
     //drive at half motor power 
     Intake0.set(0.5);
     
     // Drive straight for 5 seconds
     if (Timer.getFPGATimestamp() > driveStartTime + 0.8)
     {
       // Stop the Motors 
       Intake0.set(0);
       driveStartTime = Timer.getFPGATimestamp(); 
       robotState = AutoStates.FIRST_DRIVE; 
     }
     break;
      case FIRST_DRIVE:
        // drive at half motor power 
        LeftMaster.set(.5 );
        RightMaster.set(.5);

        Intake2.set(-0.7);
        Intake4.set(-0.7);
        
        // if ((!virtualirbeam4.get())) {
        //  irbeam4tripped = 1;
        //   System.out.println("DIO 4 input detected");
        //   Intake4.set(0);
        // }
        // else {
  
        // }
        // if ((!virtualirbeam5.get())){
        //   irbeam5tripped = 1;
        //   System.out.println("DIO 5 input detected");
        //   Intake2.set(0);
        // }
        // else {
  
        // }
        // Drive straight for 5 seconds
        if (Distance2 < -20000)
        {
         
          // Stop the Motors 
          LeftMaster.set(0);
          RightMaster.set(0);
          Intake2.set(0);
        Intake4.set(0);
        
          driveStartTime = Timer.getFPGATimestamp(); 
          robotState = AutoStates.FIRST_TURN; 
        }
        break; 
    case FIRST_TURN: 
    // compensate for any gyro movement before 
    if ((Gyro.getAngle() > -150)){
    LeftMaster.set(.3);
    RightMaster.set(-.3);
    }
    // Turn deadband If within 1.0 degrees stop turning
  //   if (Math.abs(error) < 1.0 )
  //   {
  //    error = 0; 
  //  }
    //double turnPower = error * kP_turn; 
    
    // Break out of state if Error is within deadband OR we have been turning for to long (Something has gone wrong!)
    if ((Gyro.getAngle() < -157))
    {
      LeftMaster.set(0);
      RightMaster.set(0);
      driveStartTime = Timer.getFPGATimestamp(); 
      robotState = AutoStates.SHOOT_BALLS; 
    }
    break; 
  
        case SHOOT_BALLS:
        LeftShooter.set(-1);
        RightShooter.set(1);

        if (Timer.getFPGATimestamp() > driveStartTime + 1)
        {
        Intake1.set(-.5);
        Intake2.set(-.5);
        Intake3.set(-.5);
        Intake4.set(-.5);
        Intake5.set(0.5);
          
          driveStartTime = Timer.getFPGATimestamp(); 
          robotState = AutoStates.STOP_BALLS; 
        }
         
     case STOP_BALLS:
     if (Timer.getFPGATimestamp() > driveStartTime + 3)
     {
      Intake1.set(0);
      Intake2.set(0);
      Intake3.set(0);
      Intake4.set(0);
      Intake5.set(0);
      LeftShooter.set(0);
      RightShooter.set(0);
      driveStartTime = Timer.getFPGATimestamp(); 
          robotState = AutoStates.END_AUTO; 
     }
   } 
      }  

       
      
  



  @Override
  public void teleopInit() {
    
  }

  
  
  @Override
  public void teleopPeriodic() {
    irbeam1tripped = 0;
    irbeam2tripped = 0;
    irbeam3tripped = 0;
    irbeam4tripped = 0;
    irbeam5tripped = 0;

  
  // Drive Motors
double left = deadband(Xbox.getRawAxis(1),0.1);
    double right = deadband(Xbox.getRawAxis(5),0.1);
    
    double ShooterLeft = Controller.getRawAxis(3);
    double ShooterRight = Controller.getRawAxis(3);
    RightMaster.set(-right);
    LeftMaster.set(-left);
  

    if (Xbox.getRawButtonPressed(8)) {
      SensorOn = !SensorOn;
      System.out.println("trueOn");
     // buttonpressed = buttonpressed + 1
    }

    if (Xbox.getRawButtonPressed(7)) {
      AutoOn = !AutoOn;
      System.out.println("trueOn");
     // buttonpressed = buttonpressed + 1
    }
  
  
      
    //Intake
    // 0 balls
//     if(Controller.getRawButton(8)){

//       Intake1.set(-.5);
//       Intake2.set(-.5);
//       Intake3.set(-.5);
//       Intake4.set(-.5);
//       Intake5.set(.5);
//       // if (!virtualirbeam1.get()) {
//       //   Intake1.set(0);
//       //   Intake2.set(0);
//       //   Intake3.set(0);
//       //   Intake4.set(0);
//       //   Intake5.set(0);
//       // }
//   }
// //   // 1 balls 

// // 2 balls 
// else if(Controller.getRawButton(10)){

//   Intake2.set(-.75);
//   Intake3.set(-.75);
//   Intake4.set(-.75);
//   Intake5.set(0.75);
// }  
// // 3 balls 
// else if(Controller.getRawButton(12)){

//   Intake2.set(-.75);
//   Intake4.set(-.75);
//   Intake5.set(0.75);
// }
// // 4 balls 
// else if(Controller.getRawButton(7)){

//   Intake2.set(-.75);
//   Intake4.set(-0.75);
  
// }
// //5 balls 
// else if(Controller.getRawButton(9)){

//   Intake2.set(-.75);
  
// }



// Intake 0
if(Controller.getRawButton(5)){
  Intake0.set(0.7); // out
}
else if(Controller.getRawButton(6)){
  Intake0.set(-0.7); // in
}
else{
  Intake0.set(0);
}
// Shooter
if (Xbox.getRawButton(6)){
  LeftShooter.set(-1);
  RightShooter.set(1);

}
else{
  RightShooter.set(-0);
  LeftShooter.set(0);
}
    //Winch
    if (Xbox.getRawButton(1)){ // Down change back to 1
      Winch.set(1);
    }
   else if (Xbox.getRawButton(4)){ // Up
      Winch.set(-1);
    }
    else{
      Winch.set(0);
    }
    //Flag
    if(Xbox.getRawButton(2)){ // Up
      Flag.set(-1);
    }
    else if(Xbox.getRawButton(3)){ // Down
      Flag.set(1);
    }
    else {
      Flag.set(0);
    }
}

  

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {



  }
}

