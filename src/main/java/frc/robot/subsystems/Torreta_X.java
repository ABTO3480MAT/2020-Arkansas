/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team3480.lib.control.PIDController;
import com.team3480.lib.drivers.LazySparkMax;
import com.team3480.lib.drivers.SparkMaxFactory;
import com.team3480.lib.util.ReflectingCSVWriter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Torreta_X extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

        // Hacer que se imprima el valor del encoder de la torreta.
    }

    private static Torreta_X mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private LazySparkMax Motor_Neo_Torreta;
    private CANPIDController PID_Torreta;
    private CANEncoder Torreta_Encoder;
    private double Posicion_Torreta_X;
    private double Reduccion= 700;
    private double Posicion;
    private double Desfase_X;
    private double validTarget;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Torreta_X getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Torreta_X();
        }
        return mInstance;
    }

    private Torreta_X() {
        mPeriodicIO = new PeriodicIO();
        Motor_Neo_Torreta = SparkMaxFactory.createDefaultSparkMax(Constants.Torreta_X_Constants.kid_Motor_Torreta);

       Torreta_Encoder = Motor_Neo_Torreta.getEncoder();
       Torreta_Encoder.setPosition(0);
       PID_Torreta = Motor_Neo_Torreta.getPIDController();
        PID_Torreta.setP(Constants.Torreta_X_Constants.kP);
        PID_Torreta.setI(Constants.Torreta_X_Constants.kI);
        PID_Torreta.setD(Constants.Torreta_X_Constants.kD);
        PID_Torreta.setIZone(Constants.Torreta_X_Constants.kIz);
        PID_Torreta.setFF(Constants.Torreta_X_Constants.kFF);
        PID_Torreta.setOutputRange(Constants.Torreta_X_Constants.kMinOutput, Constants.Torreta_X_Constants.kMaxOutput);
        
    }

    public void Calculate_And_Move_Torreta_X(double Apuntar, double Manual_X) {
        
        if(Apuntar>.2){

            if(validTarget==1){
            //Posicion_Torreta_X =  ((Posicion - Desfase_X)/360)*Reduccion;
            }else{
                //Posicion_Torreta_X= -300;
            }





           ///TOPES DE PROGRAMACION//   
           /*if(Torreta_Encoder.getPosition()<=-400){
            Posicion_Torreta_X=-399;
           }else if(Torreta_Encoder.getPosition()>=.20){
               Posicion_Torreta_X=.19;
           }     */   
        }else{
            //Posicion_Torreta_X=0;
        }

        if(Manual_X>.15 || Manual_X<-.15){
            Posicion_Torreta_X = (Manual_X * -25) + (Posicion/360) * Reduccion;
        }

        if(Torreta_Encoder.getPosition()<=-400){
            Posicion_Torreta_X=-399;
           }else if(Torreta_Encoder.getPosition()>=150){
               Posicion_Torreta_X=149;
           }
           System.out.println("TorretaX: "+Posicion);
           System.out.println("SetPoint: "+Posicion_Torreta_X);

    }
    /*
    public void Posicionamiento_Manual_X(double Angulo){
        //Posicion_Torreta_X = (Angulo/360)*Reduccion;
        if(Angulo==270){
            if(Torreta_Encoder.getPosition()>=100){
                Posicion_Torreta_X= 0;
            }else{
                Posicion_Torreta_X= .20;
            }
        }else if(Angulo==90){
            if(Torreta_Encoder.getPosition()<=-400){
                Posicion_Torreta_X= 0;
            }else{
                Posicion_Torreta_X= -.20;
            }
        }else if(Angulo==-1){
            Posicion_Torreta_X=0;
        }else{
            //Posicion_Torreta_X=0;
        }

        //System.out.println("Encoder torreta X" + Torreta_Encoder.getPosition());
        
    }
    */

    

    @Override
    public synchronized void readPeriodicInputs() {
        Posicion = (Torreta_Encoder.getPosition()/Reduccion)*360;
        Desfase_X = Limelight.getInstance().getAngleX();
        validTarget = Limelight.getInstance().getTarget();

    }

    @Override
    public synchronized void writePeriodicOutputs() {
        
      // Motor_Neo_Torreta.set(ControlType.kDutyCycle, Posicion_Torreta_X);
        
       
        PID_Torreta.setReference(Posicion_Torreta_X, ControlType.kPosition);
        
    }

    @Override
    public void zeroSensors() {
        Torreta_Encoder.setPosition(0);

    }

    @Override
    public synchronized void stop() {
        Posicion_Torreta_X = 0;
        PID_Torreta.setReference(0, ControlType.kDutyCycle);

    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Torreta_X.this) {
                    stop();
                    Motor_Neo_Torreta.setIdleMode(IdleMode.kBrake);
                    Posicion_Torreta_X = 0;
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Torreta_X.this) {
                    
                    if(Logging && mCSVWriter != null){mCSVWriter.add(mPeriodicIO);}
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
                Motor_Neo_Torreta.setIdleMode(IdleMode.kBrake);
                if(Logging){stopLogging();}
            }
        });
    }

    public synchronized void startLogging() {
        if (mCSVWriter == null) {
            
            //mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/TEST-LOGS.csv", PeriodicIO.class);
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
    public void outputTelemetry() {
        SmartDashboard.putNumber("Angulo X", Posicion_Torreta_X);
        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

}