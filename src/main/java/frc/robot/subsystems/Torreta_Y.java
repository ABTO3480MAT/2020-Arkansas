/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team3480.lib.drivers.LazySparkMax;
import com.team3480.lib.drivers.SparkMaxFactory;
import com.team3480.lib.util.ReflectingCSVWriter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Torreta_Y extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Torreta_Y mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private LazySparkMax Motor_Neo_Torreta;
    private double Posicion_Torreta_Y;
    private CANPIDController PID_Torreta_Y;
    private CANEncoder Torreta_Y_Encoder;
    private double Reduccion= 81;
    private double Posicion;
    private double Desfase_Y;
    private double a =-2.7, b=30.15 , c=-113.1;
    private double validTarget , Angulo_correcto, Distancia,Distancia_Fija;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Torreta_Y getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Torreta_Y();
        }
        return mInstance;
    }

    private Torreta_Y() {
        mPeriodicIO = new PeriodicIO();
        Motor_Neo_Torreta= SparkMaxFactory.createDefaultSparkMax(Constants.Torreta_Y_Constants.kid_Motor_Torreta);
        Torreta_Y_Encoder = Motor_Neo_Torreta.getEncoder();
       Torreta_Y_Encoder.setPosition(0);
       PID_Torreta_Y = Motor_Neo_Torreta.getPIDController();
        PID_Torreta_Y.setP(Constants.Torreta_Y_Constants.kP);
        PID_Torreta_Y.setI(Constants.Torreta_Y_Constants.kI);
        PID_Torreta_Y.setD(Constants.Torreta_Y_Constants.kD);
        PID_Torreta_Y.setIZone(Constants.Torreta_Y_Constants.kIz);
        PID_Torreta_Y.setFF(Constants.Torreta_Y_Constants.kFF);
        PID_Torreta_Y.setOutputRange(Constants.Torreta_Y_Constants.kMinOutput, Constants.Torreta_Y_Constants.kMaxOutput);

    }
    
    public void Calculate_And_Move_Torreta_Y(double Apuntar , double ManualY){

        if(Apuntar<.20){
            //Posicion_Torreta_Y = 0;
        }
        else if(Apuntar>.40 &&Apuntar<.80){
            
            Distancia_Fija= Distancia;

        }else if(Apuntar>=.8){
            //Desfase_Y= Constants.Limelight_Constants.kCamaraAnguloVertical-Math.toDegrees(Math.atan((3-Constants.Limelight_Constants.kAlturaCamara)/Distancia)); 
                //Posicion_Torreta_Y= -2.7*Distancia_Fija*Distancia_Fija+30.14*Distancia_Fija-106.1;
        }
        if(ManualY>.15 || ManualY<-.15){
            Posicion_Torreta_Y= Posicion+(15*ManualY);
        }

        //Topes
        if(Posicion_Torreta_Y>=0){
            Posicion_Torreta_Y=0;
        }
        if(Posicion_Torreta_Y<=-80){
            Posicion_Torreta_Y=-80;
        }
        
    

    }
    



   
    /*
    public void Posicionamiento_Manual_Y(double Angulo){
       /*
        if (stick>=.20){
            Posicion_Torreta_Y = stick*.10;
        }else if(stick<=-20){
            Posicion_Torreta_Y= stick*.10;
        }else{
            Posicion_Torreta_Y = 0;
        }
        */
        //Posicion_Torreta_Y = (Angulo/360)*Reduccion;
        /*
        if(Angulo==0){
            if(Torreta_Y_Encoder.getPosition()<=-120){
                Posicion_Torreta_Y= 0;
            }else{
            Posicion_Torreta_Y=-.20;
            }
        }else if(Angulo==180){
            if(Torreta_Y_Encoder.getPosition()>=0){
                Posicion_Torreta_Y= 0;
            }else{
            Posicion_Torreta_Y=.20;
            }
        }else if(Angulo==-1){
            Posicion_Torreta_Y=0;
        }else{
            //Posicion_Torreta_Y=0;
        }
        
        //System.out.println("Encoder torreta Y" + Torreta_Y_Encoder.getPosition());
    }
    */
    
    

    public double getAngulo_Motor_Y(){
        return Posicion;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        //Posicion = (Torreta_Y_Encoder.getPosition()/Reduccion)*360;
        Posicion = Torreta_Y_Encoder.getPosition();
        //Desfase_Y = Limelight.getInstance().getAngleY();
        validTarget =  Limelight.getInstance().getTarget();
        Angulo_correcto= Limelight.getInstance().getAngulo_Controlado();
        Distancia= Limelight.getInstance().getDistanciaCorrecta();


    }

    @Override
    public synchronized void writePeriodicOutputs() {
       
        PID_Torreta_Y.setReference(Posicion_Torreta_Y, ControlType.kPosition);
       // Motor_Neo_Torreta.set(ControlType.kDutyCycle, Posicion_Torreta_Y);
    }

    @Override
    public void zeroSensors() {

    }

    @Override
    public synchronized void stop() {
        Posicion_Torreta_Y = 0;
        PID_Torreta_Y.setReference(0, ControlType.kDutyCycle);

    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Torreta_Y.this) {
                    stop();
                    Motor_Neo_Torreta.setIdleMode(IdleMode.kBrake);
                    Posicion_Torreta_Y = 0;
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Torreta_Y.this) {
                    
                    if(Logging && mCSVWriter != null){mCSVWriter.add(mPeriodicIO);}
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
                Motor_Neo_Torreta.setIdleMode(IdleMode.kCoast);
                if(Logging){stopLogging();}
            }
        });
    }

    public synchronized void startLogging() {
        if (mCSVWriter == null) {
           // mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/TEST-LOGS.csv", PeriodicIO.class);
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
        SmartDashboard.putNumber("Angulo Y", Posicion+Constants.kCamaraAnguloVertical);
        SmartDashboard.putNumber("Angulo Y Limelight", Desfase_Y);
        SmartDashboard.putNumber("Posicion Y", Posicion_Torreta_Y);
        SmartDashboard.putNumber("Encoder Y", Torreta_Y_Encoder.getPosition());
        SmartDashboard.putNumber("OPERACION ", Posicion_Torreta_Y);

        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

}