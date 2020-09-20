/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import java.util.List;

import javax.sql.rowset.spi.SyncResolver;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team3480.lib.control.PIDController;
import com.team3480.lib.control.PathFollower;
import com.team3480.lib.control.PathSegment;
import com.team3480.lib.control.Vector2;
import com.team3480.lib.drivers.LazySparkMax;
import com.team3480.lib.drivers.Robot_Heading;
import com.team3480.lib.drivers.SparkMaxFactory;
import com.team3480.lib.util.ReflectingCSVWriter;
import com.team3480.lib.util.Utils;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;
import jdk.dynalink.beans.MissingMemberHandlerFactory;

public class Drive extends AbtomatSubsystem {

    public enum DriveMode{
        Drivers,
        PathFollow,
        OpenLoopMode
    }

    public static class PeriodicIO {
        // INPUTS
        public double timestamp=0;
        public double deltaTime=0;

        public double left_position_ticks=0;
        public double right_position_ticks=0;

        public double left_meterspersecond=0;
        public double right_meterspersecond=0;
        public double gyro_heading=0;

        //PathFollow
        public double left_pathfollowVelocity=0;
        public double right_pathfollowVelocity=0;

        // OUTPUTS
        public double left_demand;
        public double right_demand;
    }

    private static Drive mInstance;  //instancia unica 

    private PeriodicIO mPeriodicIO;
    private boolean Logging=true;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    // hardware
    private final LazySparkMax mLeftMaster, mRightMaster, mLeftSlave, mRightSlave;
    private final Encoder mLeftEncoder, mRightEncoder;
    private final Robot_Heading mheading;

    //control variables
    public DriveMode driveMode;
    public boolean mIsBreakMode = false;
    public boolean mIsLimited = false;

    //path follower
    private PathSegment path=null;
    private PathFollower mPathFollower = null;
    private int pathconter=0;
    private boolean endPath=false;
    //private boolean endingPath=false;
    //private double robotoldvelocity=0;
    private PIDController pidL;
    private PIDController pidR;

    //Drive forzado open loop
    private double BaseSpeedForceL=0;
    private double BaseSpeedForceR=0;

    /////Drivers manejando
    public boolean isPathFollowInverted=false; //para invertir el path follow

    ////ErrorPath
    public boolean endWithErrorPath=false;

    private double Poder, Left_Side, Right_Side;
    private int Invertir=1;
    private double Reducir=1;
    
    public synchronized static Drive getInstance() { //para leer la instancia unica 
        if (mInstance == null) {
            mInstance = new Drive();
        }
        return mInstance;
    }

    private void configureSpark(LazySparkMax sparkMax, boolean left) { //funcion para configurar los sparks
        sparkMax.setInverted(left);
        sparkMax.enableVoltageCompensation(12.0);
        sparkMax.setClosedLoopRampRate(Constants.kDriveVoltageRampRate);
    }

    private Drive() {
        mPeriodicIO = new PeriodicIO();

        // start all sparks

        mRightMaster = SparkMaxFactory.createDefaultSparkMax(Constants.kid_Motor_Right_Master);
        configureSpark(mRightMaster, false);
        mRightSlave = SparkMaxFactory.createPermanentSlaveSparkMax(Constants.kid_Motor_Right_Slave, mRightMaster, false);
        configureSpark(mRightSlave, false);
        mLeftMaster = SparkMaxFactory.createDefaultSparkMax(Constants.kid_Motor_Left_Master);
        configureSpark(mLeftMaster, true);
        mLeftSlave = SparkMaxFactory.createPermanentSlaveSparkMax(Constants.kid_Motor_Left_Slave, mLeftMaster, false);
        configureSpark(mLeftSlave, true);

        mLeftEncoder = new Encoder(2, 3, false);
        mRightEncoder = new Encoder(0, 1, true); 

        mLeftEncoder.setDistancePerPulse(Constants.kDriveWheelDiameter * Math.PI / Constants.kDriveEncoderPPR);
        mRightEncoder.setDistancePerPulse(Constants.kDriveWheelDiameter * Math.PI / Constants.kDriveEncoderPPR);

        mheading = Robot_Heading.getInstance();

        pidL = new PIDController(Constants.kDriveSpeed_kp,Constants.kDriveSpeed_ki,Constants.kDriveSpeed_kd, Constants.kDriveSpeed_epsilon);
        pidL.outlimitPositive=1;
        pidL.outlimitNegative=-1;

        pidR = new PIDController(Constants.kDriveSpeed_kp,Constants.kDriveSpeed_ki,Constants.kDriveSpeed_kd, Constants.kDriveSpeed_epsilon);
        pidR.outlimitPositive=1;
        pidR.outlimitNegative=-1;
    }

    

    public synchronized void UpdateDriveSpeed(double left, double right){ //para actualizar la velocidad de los motores
        mPeriodicIO.left_demand=left;
        mPeriodicIO.right_demand=right;
        if(mPeriodicIO.left_demand>1f){mPeriodicIO.left_demand=1;}else if(mPeriodicIO.left_demand<-1){mPeriodicIO.left_demand=-1;}
        if(mPeriodicIO.right_demand>1f){mPeriodicIO.right_demand=1;}else if(mPeriodicIO.right_demand<-1){mPeriodicIO.right_demand=-1;}
    }

    @Override
    public synchronized void readPeriodicInputs() {
        mPeriodicIO.deltaTime = Timer.getFPGATimestamp()-mPeriodicIO.timestamp;
        mPeriodicIO.timestamp = Timer.getFPGATimestamp();

        if(isPathFollowInverted){
            mPeriodicIO.left_position_ticks = mRightEncoder.get()*-1.0;
            mPeriodicIO.right_position_ticks = mLeftEncoder.get()*-1.0;
            mPeriodicIO.gyro_heading = mheading.mapAngle((mheading.getRawRotation()*(Constants.kInvertNavx ? -1.0 : 1.0))+270,Robot_Heading.DataType.Rotation);
        }else{
            mPeriodicIO.left_position_ticks = mLeftEncoder.get();
            mPeriodicIO.right_position_ticks = mRightEncoder.get();
            mPeriodicIO.gyro_heading = mheading.mapAngle((mheading.getRawRotation()*(Constants.kInvertNavx ? -1.0 : 1.0))+90,Robot_Heading.DataType.Rotation);
        }

        mPeriodicIO.left_meterspersecond= mLeftEncoder.getRate();
        mPeriodicIO.right_meterspersecond = mRightEncoder.getRate();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        if(isPathFollowInverted){
            mLeftMaster.set(mPeriodicIO.right_demand*-Invertir);
            mRightMaster.set(mPeriodicIO.left_demand*-Invertir);
        }else{
            mLeftMaster.set(mPeriodicIO.left_demand*Invertir);
            mRightMaster.set(mPeriodicIO.right_demand*Invertir);
        }
    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Drive.this) {
                    driveMode = DriveMode.Drivers; //modo drivers manejando por default
                    isPathFollowInverted=false; //quita el invertido por default
                    stop();
                    if(Logging){startLogging();}
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Drive.this) {
                    switch (driveMode) {
                        case Drivers:
                            break;
                        case PathFollow:
                            PathFollowModeUpdate();
                            break;
                        case OpenLoopMode:
                            OpenLoopModeUpdate();
                            break;
                        default:
                            System.out.println("unexpected drive control state: " + driveMode);
                            break;
                    }

                    if(Logging && mCSVWriter != null){mCSVWriter.add(mPeriodicIO);}
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
                if(Logging){stopLogging();}
            }
        });
    }

    public synchronized void setBreakMode(boolean shouldEnable, boolean forcestate) {
        if (mIsBreakMode != shouldEnable || forcestate){
            mIsBreakMode = shouldEnable;
            IdleMode mode = shouldEnable ? IdleMode.kBrake : IdleMode.kCoast;
            mRightMaster.setIdleMode(mode);
            mRightSlave.setIdleMode(mode);
            mLeftMaster.setIdleMode(mode);
            mLeftSlave.setIdleMode(mode);
        }
    }
    
    public synchronized boolean isBreakMode(){
        return mIsBreakMode;
    }

    public synchronized boolean isLimited(){
        return mIsLimited;
    }
    public synchronized double getHeading() { //regresamos el angulo de rotacion del robot
        return mPeriodicIO.gyro_heading;
    }

    public synchronized void resetEncoders() { //reseteamos los encoders
        mLeftEncoder.reset();
        mRightEncoder.reset();
    }

    public double VelocityToPWM(double velocity){ //velocidad lineal a pwm(porcentage)
        return (velocity/(Constants.kDriveWheelRadius*Constants.kMaxAngularSpeed));
    }

    private static double rotationsToMeters(double rotations) { //rotaciones a metros
        return rotations * (Constants.kDriveWheelDiameter * Math.PI);
    }

    public double getLeftEncoderRotations() { //regresa las rotaciones del lado izquierdo
        return mPeriodicIO.left_position_ticks / Constants.kDriveEncoderPPR;
    }

    public double getRightEncoderRotations() { //regresa las rotaciones del lado derecho
        return mPeriodicIO.right_position_ticks / Constants.kDriveEncoderPPR;
    }

    public double getLeftEncoderDistance() { //regresa la distancia en metros del lado izquierdo
        return rotationsToMeters(getLeftEncoderRotations());
    }

    public double getRightEncoderDistance() { //regresa la distancia en metros del lado derecho
        return rotationsToMeters(getRightEncoderRotations());
    }

    public double getLeftLinearVelocity() { //regresa la velocidad lineal del lado izquierdo
        return mPeriodicIO.left_meterspersecond;
    }

    public double getRightLinearVelocity() { //regresa la velocidad lineal del lado derecho
        return mPeriodicIO.right_meterspersecond; 
    }

    @Override
    public void zeroSensors() { //reseta los sensores
        mheading.resetFusedHeading();
        resetEncoders();
        mPeriodicIO = new PeriodicIO();
    }

    @Override
    public synchronized void stop() { //detiene el drive
        if(mPeriodicIO!=null){
            UpdateDriveSpeed(0,0);
        }
        mLeftMaster.set(ControlType.kDutyCycle, 0);
        mRightMaster.set(ControlType.kDutyCycle, 0);
    }

    @Override
    public boolean checkSystem() { //funcion para realizar el chequeo de los subsistemas
        return(true);
    }

    public synchronized void startLogging() { //comienza el debug en el archivo
        if (mCSVWriter == null) {
            mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/Drive-LOGS.csv", PeriodicIO.class);
        }
    }

    public synchronized void stopLogging() { //detiene el debug en el archivo
        if (mCSVWriter != null) {
            mCSVWriter.flush();
            mCSVWriter = null;
        }
    }

    @Override
    public void outputTelemetry() { //aqui ponemos todo lo que se imprima en el dashboard o debugs
        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

    //////////////MANUAL CONTROL//////////////////

    public void Main_Move(double Trigger_R, double Trigger_L, double Rotacion ){
        

        //System.out.println("Trigger derecho" + Trigger_R);
        //System.out.println("Trigger izquierdo" + Trigger_L);
        //System.out.println("Steech" + Rotacion);
        if(Trigger_R > .15 || Trigger_L < -.15){

            Poder = Trigger_R + Trigger_L;
            Left_Side = -Poder;
            Right_Side = -Poder;
            
            if (Rotacion > .15 || Rotacion < -.15){
                if(Invertir == 1){

                    Left_Side = -Poder*Rotacion * Invertir - Poder;
                    Right_Side = Poder*Rotacion * Invertir - Poder;
                
                }else{

                    //Se multiplica por .6 para que el chasis no derrape
                    //Esto se hace por su configuracion de llantas
                    Left_Side = -Poder*Rotacion * Invertir * .6 - Poder;
                    Right_Side = Poder*Rotacion * Invertir * .6 - Poder;
                
                }
            }
      
        }else if (Rotacion > .20 || Rotacion < -.20){

            Right_Side = -Rotacion * Invertir *.7;
            Left_Side = Rotacion * Invertir *.7;

        }else{
            //Si ninguno de los valores cumple con lo minimo de poder o tolerancia
            //los valores que se enviaran seran 0
            Left_Side = 0;
            Right_Side = 0;
      
        }
            //Esta operacion hace que los valores que se envien 
            //al motor no sean superiores a 1 y/o inferiores a -1
            if(Left_Side>1){
                Left_Side = 1;
            }else if(Left_Side<-1){
                Left_Side = -1;
            }
            if(Right_Side>1){
                Right_Side = 1;
            }else if(Right_Side<-1){
                Right_Side = -1;
            }
     
            Left_Side *= Reducir;
            Right_Side *= Reducir;

            //Left_Side *= Invertir;
            //Right_Side *= Invertir;

            UpdateDriveSpeed(Left_Side, Right_Side);

            //System.out.println("Izquierda: " + Left_Side);
            //System.out.println("Derecha: " +Right_Side);
            
        }

    public synchronized void Invertir_Chasis(boolean Estado_invertir){
        //System.out.println("Invertir");
        if (Estado_invertir== true){
            Invertir *= -1;
        }
    }

    public synchronized void Reducir_Chasis(boolean Estado_Reducir){
        //System.out.println("Reducir");
        if (Estado_Reducir && Reducir ==1){
            Reducir=.5;
        }else{
            Reducir=1;
        }
    }


    //############Force mode Code#####################//

    public synchronized void UpdateOpenLoopModeSpeed(double left, double right){
        BaseSpeedForceL=left;
        BaseSpeedForceR=right;
    }

    public synchronized void OpenLoopModeUpdate(){ //nos movemos sin ningun control (pid)
        UpdateDriveSpeed(BaseSpeedForceL,BaseSpeedForceR);
    }
    //##################################################//


    //###############Path Follow Methods##################//
    public synchronized void ResetPeriodicStates(){
        mPeriodicIO.left_position_ticks=0;
        mPeriodicIO.right_position_ticks=0;
        mPeriodicIO.left_meterspersecond=0;
        mPeriodicIO.right_meterspersecond=0;
        mPeriodicIO.gyro_heading=0;
        mPeriodicIO.left_pathfollowVelocity=0;
        mPeriodicIO.right_pathfollowVelocity=0;
        mPeriodicIO.right_demand=0;
        mPeriodicIO.left_demand=0;
    }

    public synchronized void SetPath(PathSegment newpath){  //actualiza el path
        endPath=false;
        //endingPath=false;
        path=newpath;
        mPathFollower = new PathFollower();
        newpath.Reverse();
        //robotoldvelocity=0;
        pathconter=0;
    }

    public synchronized void PathFollowModeUpdate(){
        if(path!=null){
            pathconter = mPathFollower.GetClosestPoint(path.points);
            Vector2 lookahead = mPathFollower.GetLookAheadPoint(path.points,path.LookAheadDistance);
            if(lookahead!=null){
                double curvature = mPathFollower.CurvatureArc(lookahead);
                
               /* /////rampa de aceleracion
                if(Math.abs(path.velocities.get(pathconter)-robotoldvelocity)>Constants.kDriveRampPathFollow){
                    if((path.velocities.get(pathconter)-robotoldvelocity)>0){
                        robotoldvelocity+=Constants.kDriveRampPathFollow;
                    }else if((path.velocities.get(pathconter)-robotoldvelocity)<0){
                        robotoldvelocity-=Constants.kDriveRampPathFollow;
                    }
                }else{
                    robotoldvelocity=path.velocities.get(pathconter);
                }
  
                //////////////////////////*/
                List<Double> velo = mPathFollower.CalculateVelocity(path.velocities.get(pathconter),curvature,Constants.kDriveWheelTrackWidth, Constants.kMaxVelocity*path.MaxVelAlLimiter);
     
                if(Constants.kDriveSpeedPIDActive){

                    double ffL = Constants.kDriveSpeed_kff * VelocityToPWM(velo.get(0));
                    double ffR = Constants.kDriveSpeed_kff * VelocityToPWM(velo.get(1));

                    UpdateDriveSpeed(pidL.Get(velo.get(0),getLeftLinearVelocity(), mPeriodicIO.deltaTime, ffL),
                                     pidR.Get(velo.get(1),getRightLinearVelocity(), mPeriodicIO.deltaTime, ffR));
                }else{
                    UpdateDriveSpeed(VelocityToPWM(velo.get(0)),VelocityToPWM(velo.get(1)));
                }

                mPeriodicIO.left_pathfollowVelocity = velo.get(0);
                mPeriodicIO.right_pathfollowVelocity = velo.get(1);

                if((Utils.Distance(path.points.get(path.points.size()-1), Odometry.getInstance().mPeriodicIO.pose.position)<=0.1)){
                    if(mPeriodicIO.left_demand==0 && mPeriodicIO.right_demand==0){
                        EndPath(true);
                    }
                }else if(Utils.Distance(path.points.get(pathconter), Odometry.getInstance().mPeriodicIO.pose.position)>0.7){ //si se perdio
                    System.out.println("To far to point..........");
                    EndPath(false);
                }
            }else{
                System.out.println("No lookahead point found..........");
                EndPath(false);
            }
        }
    }

    public synchronized boolean isDoneWithPath() {
        if(driveMode == DriveMode.PathFollow) {
            return endPath;
        }else {
            System.out.println("Robot is not in path following mode");
            return true;
        }
    }

    public synchronized void EndPath(boolean endcorrect){
        if(endcorrect){
            ////Activa la rampa de frenado////
            /*if(!isPathFollowInverted){
                endingPath=true;
                double getMin = Math.min(mPeriodicIO.left_demand,mPeriodicIO.right_demand);
                if(getMin<0){getMin=0;}
                UpdateDriveSpeed((getMin-(Constants.kDriveRampPathFollow * 0.2)),(getMin-(Constants.kDriveRampPathFollow * 0.2)));
                if(mPeriodicIO.right_demand<0){mPeriodicIO.right_demand=0;}
                if(mPeriodicIO.left_demand<0){mPeriodicIO.left_demand=0;}
            }else{
                endingPath=true;
                double getMin = Math.max(mPeriodicIO.left_demand,mPeriodicIO.right_demand);
                if(getMin>0){getMin=0;}
                UpdateDriveSpeed((getMin+(Constants.kDriveRampPathFollow * 0.2)),(getMin+(Constants.kDriveRampPathFollow * 0.2)));
                if(mPeriodicIO.right_demand>0){mPeriodicIO.right_demand=0;}
                if(mPeriodicIO.left_demand>0){mPeriodicIO.left_demand=0;}
            }
            ////////////////////////////////////
            if(mPeriodicIO.left_demand==0 && mPeriodicIO.right_demand==0){ //ya termino de frenar
                System.out.println("******Robot path end correct :)******");
                endPath=true;
                path=null;
            }
        }else{
            System.out.println("*****Robot path end with error*****");
            endPath=true;
            path=null;*/
            System.out.println("Robot path end correct");
            endPath=true;
            path=null;
        } else {
            endPath=true;
            endWithErrorPath=true;
            path=null;
        }
    }


    public synchronized void ResetErrorPathFlag(){
        endWithErrorPath=false;
    }

    public synchronized boolean GetErrorPathFlag(){
        return(endWithErrorPath);
    }

    public synchronized boolean hasPassedMarker(String marker) {
        for(int x=0;x<path.markers.size();x++){
            if(marker==path.markers.get(x).marker){
                if(path.markers.get(x).pos>=pathconter){
                    return(true);
                }
                return(false);
            }
        }
        return(false);
    }
    //#################################################//

}