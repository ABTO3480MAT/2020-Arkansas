package frc.robot;

import java.util.Optional;

import com.team3480.lib.util.CrashTracker;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.Constants.Balanceador_Constants;
import frc.robot.auto.AutoModeExecutor;
import frc.robot.auto.AutoModeSelector;
import frc.robot.auto.modes.AutoModeBase;
import frc.robot.loops.Looper;
import frc.robot.subsystems.*;


public class Robot extends TimedRobot {

  private final Looper mEnabledLooper = new Looper();
  private final Looper mDisabledLooper = new Looper();

  private final SubsystemManager mSubsystemManager = SubsystemManager.getInstance();

  //Para leer los controles
  private final RobotContainer robotcontainer = new RobotContainer();

  // subsystems
  private final Odometry mOdometry = Odometry.getInstance();
  private final Drive mDrive = Drive.getInstance();
  private final Climber mClimber = Climber.getInstance();
  //private final Balanceador mBalanceador = Balanceador.getInstance();
  private final Intake mIntake = Intake.getInstance();
  private final Transportador mTransportador = Transportador.getInstance();
  private final Elevador_Pelotas mElevador_Pelotas = Elevador_Pelotas.getInstance();
  private final Shooter mShooter = Shooter.getInstance();
  private final Torreta_X mTorreta_X = Torreta_X.getInstance();
  private final Torreta_Y mTorreta_Y = Torreta_Y.getInstance();
  



  //private final testSubsystem mTestSub = testSubsystem.getInstance();


  public Robot(){ //constructor
    CrashTracker.logRobotConstruction();
  }

  //para la seleccion y correr el autonomo
  private AutoModeSelector mAutoModeSelector = new AutoModeSelector();
  private AutoModeExecutor mAutoModeExecutor;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    try {
      CrashTracker.logRobotInit();

      mSubsystemManager.setSubsystems(
        mOdometry,
        mDrive,
        mClimber,
        //mBalanceador,
        mIntake,
        mTransportador,
        mElevador_Pelotas,
        mShooter,
        mTorreta_X,
        mTorreta_Y
      ); // (subsystem1,subsystem2,...,subsystemN)

      mDrive.zeroSensors(); //reseteamos sensores
      mOdometry.zeroSensors();
      mClimber.zeroSensors();
      //mBalanceador.zeroSensors();
      mTransportador.zeroSensors();
      mElevador_Pelotas.zeroSensors();
      mShooter.zeroSensors();
      mTorreta_X.zeroSensors();
      mTorreta_Y.zeroSensors();




      mSubsystemManager.registerEnabledLoops(mEnabledLooper);
      mSubsystemManager.registerDisabledLoops(mDisabledLooper);
      mAutoModeSelector.updateModeCreator();
    }catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
      throw t;
    } 
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    try {
        mAutoModeSelector.outputToSmartDashboard();
        mSubsystemManager.outputToSmartDashboard();
    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
    try {
      CrashTracker.logDisabledInit();
      mEnabledLooper.stop();

      // Reset all auto mode state.
      if (mAutoModeExecutor != null) {
          mAutoModeExecutor.stop();
      }
      mAutoModeSelector.reset();
      mAutoModeSelector.updateModeCreator();
      mAutoModeExecutor = new AutoModeExecutor();

      mDisabledLooper.start();

  } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
  }

  }

  @Override
  public void disabledPeriodic() {
    try {
      // Update auto modes
      mAutoModeSelector.updateModeCreator();

      Optional<AutoModeBase> autoMode = mAutoModeSelector.getAutoMode();
      if (autoMode.isPresent() && autoMode.get() != mAutoModeExecutor.getAutoMode()) { //si existe y es uno diferente
          System.out.println("Set auto mode to: " + autoMode.get().getClass().toString());
          mAutoModeExecutor.setAutoMode(autoMode.get());
      }

  } catch (Throwable t) {
      CrashTracker.logThrowableCrash(t);
      throw t;
  }

  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    try {
        CrashTracker.logAutoInit(); 
        mDisabledLooper.stop();

        mEnabledLooper.start();

        mAutoModeExecutor.start();
    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    /*
    //para con un switch pausar(interrumpir) o reiniciar el autonomo
    boolean signalToResume = !mControlBoard.getWantsLowGear();
    boolean signalToStop = mControlBoard.getWantsLowGear();
    // Resume if switch flipped up
    if (mWantsAutoExecution.update(signalToResume)) {
        mAutoModeExecutor.resume();
    }

    // Interrupt if switch flipped down
    if (mWantsAutoInterrupt.update(signalToStop)) {
        mAutoModeExecutor.interrupt();
    }
    */

  }

  @Override
  public void teleopInit() {
    try {
        CrashTracker.logTeleopInit();
        mDisabledLooper.stop();

        if (mAutoModeExecutor != null) { //para asegurar termine el autonomo
            mAutoModeExecutor.stop();
        }

        mEnabledLooper.start();

    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    try {
        manualControl();






      
    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    try {
        CrashTracker.logTestInit();
        System.out.println("Starting check systems.");

        mDisabledLooper.stop();
        mEnabledLooper.stop();

        if (mSubsystemManager.checkSubsystems()) {
            System.out.println("ALL SYSTEMS PASSED");
        } else {
            System.out.println("CHECK ABOVE OUTPUT SOME SYSTEMS FAILED!!!");
        }
    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
  }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }


  /**
   * Aqui tenemos el codigo del teleoperado(leemos inputs y actualizamos los estados en los subsistemas)
   */
  public void manualControl(){
      
    /////CHASIS//////
    mDrive.Main_Move(robotcontainer.Xbox_Chasis.getRawAxis(Constants.Control_Xbox_Constants.kTrigger_Right), 
    -robotcontainer.Xbox_Chasis.getRawAxis(Constants.Control_Xbox_Constants.kTrigger_Left),
    robotcontainer.Xbox_Chasis.getRawAxis(Constants.Control_Xbox_Constants.kStick_Left_Xaxis));
  
    if(robotcontainer.Xbox_Chasis.getRawButtonPressed(Constants.Control_Xbox_Constants.kButton_A)){
      mDrive.Invertir_Chasis(true);
    }
    if(robotcontainer.Xbox_Chasis.getRawButtonPressed(Constants.Control_Xbox_Constants.kButton_B)){
      mDrive.Reducir_Chasis(true);
    }
    //////////////////


    /////CLIMBER]/////
    if(robotcontainer.Xbox_Subsistemas.getRawButtonPressed(Constants.Control_Xbox_Constants.kButton_B)){
      //mClimber.SetClimbPos(Constants.Climber_Constants.kBalanceador_pos);
    }
    if(robotcontainer.Xbox_Subsistemas.getRawButtonPressed(Constants.Control_Xbox_Constants.kButton_RB)){
      //mClimber.SetClimbPos(Constants.Climber_Constants.kRuleta_pos);
    }
    if(robotcontainer.Xbox_Subsistemas.getRawButtonPressed(Constants.Control_Xbox_Constants.kButton_LB)){
      mClimber.SetClimbPos(0);
    }
    mClimber.Escalar(robotcontainer.Xbox_Subsistemas.getRawButton(Constants.Control_Xbox_Constants.kButton_Y), robotcontainer.Xbox_Subsistemas.getRawButton(Constants.Control_Xbox_Constants.kButton_A));
    /////////////////////


    /////Balanceador/////
    //mBalanceador.MoveBalanceador(robotcontainer.Xbox_Subsistemas.getPOV());
    /////////////////////


    /////INTAKE//////////
    mIntake.Accionar_Intake(robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kStick_Left_Yaxis));
    /////////////////////


    /////TRANSPORTADOR///
    mTransportador.Move_Transportador(robotcontainer.Xbox_Subsistemas.getRawButton(Constants.Control_Xbox_Constants.kButton_Back));
   
    //////ELEVADOR PELOTAS///
    mElevador_Pelotas.Transportar_Shooter(robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kTrigger_Right));
   
    //////SHOOTER///
    mShooter.Disparar(robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kTrigger_Left));
   
    //////TORRETA X///
    mTorreta_X.Calculate_And_Move_Torreta_X(robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kTrigger_Left), robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kStick_Right_Xaxis));
   
    //////TORRETA Y///
    mTorreta_Y.Calculate_And_Move_Torreta_Y(robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kTrigger_Left),robotcontainer.Xbox_Subsistemas.getRawAxis(Constants.Control_Xbox_Constants.kStick_Right_Yaxis));
  }
    
    


}
