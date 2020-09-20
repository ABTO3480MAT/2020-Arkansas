package frc.robot.subsystems;

import com.team3480.lib.control.Pose;
import com.team3480.lib.util.ReflectingCSVWriter;
import com.team3480.lib.util.Units;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Odometry extends AbtomatSubsystem{

    public static class PeriodicIO{
        public double timestamp=0;
        public Pose pose = new Pose();
    }

    private double lastdistL=0;
    private double lastdistR=0;

    private static Odometry mInstance;  //instancia unica 
    private boolean Logging=true;
    public PeriodicIO mPeriodicIO=null;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Odometry getInstance() { //para leer la instancia unica 
        if (mInstance == null) {
            mInstance = new Odometry();
        }
        return mInstance;
    }

    private Odometry() { //constructor
        mPeriodicIO = new PeriodicIO();
    }


    @Override
    public synchronized void readPeriodicInputs() {
        mPeriodicIO.timestamp = Timer.getFPGATimestamp();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        
    }

    @Override
    public void zeroSensors() {
        
    }

    @Override
    public synchronized void stop() {
        
    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Odometry.this) {
                    stop();
                    if(Logging){startLogging();}
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Odometry.this) {
                    Update();
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

    public synchronized void startLogging() {
        if (mCSVWriter == null) {
            mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/ODOMETRY-LOGS.csv", PeriodicIO.class);
        }
    }

    public synchronized void stopLogging() {
        if (mCSVWriter != null) {
            mCSVWriter.flush();
            mCSVWriter = null;
        }
    }

    @Override
    public boolean checkSystem() {
        return(true);
    }

    @Override
    public void outputTelemetry(){
        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

    private void Update() { //funcion que corre en el loop
        double actualdistL = Drive.getInstance().getLeftEncoderDistance();
        double actualdistR = Drive.getInstance().getRightEncoderDistance();
        double distance = ((actualdistL-lastdistL)+(actualdistR-lastdistR))/2;
        lastdistL=actualdistL;
        lastdistR=actualdistR;
        mPeriodicIO.pose.position.x += distance*Math.cos(Units.degrees_to_radians(Drive.getInstance().getHeading()));//guarda la posicion en x
        mPeriodicIO.pose.position.y += distance*Math.sin(Units.degrees_to_radians(Drive.getInstance().getHeading()));//guarda la posicion en y
        mPeriodicIO.pose.rotation = Drive.getInstance().getHeading(); //guarda la rotacion
        //System.out.println("ROT: " + mPeriodicIO.pose.rotation);
    }

    public void SetPose(Pose pose){ //para setear el pose
        if(mPeriodicIO!=null){
            mPeriodicIO.pose=pose;
        }
    }

    public Pose GetPose(){ //para regresar el pose
        if(mPeriodicIO!=null){
            return mPeriodicIO.pose;
        }
        return null;
    }

    public void ResetDistances(){ //resetea las distancias(las ultimas que guardo)
        lastdistL=0;
        lastdistR=0;
    }

}