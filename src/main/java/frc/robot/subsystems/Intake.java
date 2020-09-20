/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.ControlType;
import com.team3480.lib.util.ReflectingCSVWriter;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Intake extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS
        


        // OUTPUTS
        
        
    }

    private static Intake mInstance;  //instancia unica 
    private boolean Logging=false;
    private PeriodicIO mPeriodicIO;
    private TalonSRX Motor_Intake_TSRX;
    private Solenoid S_Intake_Piston;
    private DigitalInput IR_1,IR_2,IR_3;
    private double Power_Intake=0;
    private boolean Estado_Piston=false;
    private int Contador_Pelotas;
    private Compressor compresor;
    public static boolean Tragando=false;
    public static boolean Escupiendo = false;


    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Intake getInstance() { //para leer la instancia unica 
        if (mInstance == null) {
            mInstance = new Intake();
        }
        return mInstance;
    }

    private Intake() {
        mPeriodicIO = new PeriodicIO();
        Motor_Intake_TSRX = new TalonSRX(Constants.kid_Motor_Intake);
        S_Intake_Piston = new Solenoid(Constants.kid_Piston_Intake);
        compresor = new Compressor(0);
        compresor.setClosedLoopControl(true);

    }


    public synchronized void Accionar_Intake(double stick){
        //System.out.println("Stick intake" + stick);
        if(stick <= -.20){
            Power_Intake = -.55;
            Estado_Piston = true;
            Tragando=false;
            //System.out.println("Accionar intake");
        }else if(stick >= .20){
            Power_Intake = 1;
            Estado_Piston = true;
            Tragando=true;
        }else{
            Power_Intake=0;
            Tragando=false;
            Estado_Piston = false;
        }

    }

    /*public void Retraer_Piston(boolean button){

        if(button){
            Estado_Piston= false;
        }

    }*/

    /*public void Activar_Compresor(){
        if (compresor.getPressureSwitchValue()){

            compresor.start();
      
          }else{
            
        }
    }*/

    public boolean GetTragandoEstado(){
        return Tragando;
    }


    @Override
    public synchronized void readPeriodicInputs() {
    }

    @Override
    public synchronized void writePeriodicOutputs() {
       // System.out.println("INTAKEEEEEEEE");

        if(Estado_Piston){
            S_Intake_Piston.set(true);
            //System.out.println("BAJANDO PISTON");
        }else{
            S_Intake_Piston.set(false);
            //System.out.println("SUBIENDO PISTON");
        }
        Motor_Intake_TSRX.set(ControlMode.PercentOutput, Power_Intake);
    }

    @Override
    public void zeroSensors() {
    }

    @Override
    public synchronized void stop() {

        Power_Intake=0;
        Estado_Piston=false;
        Motor_Intake_TSRX.set(ControlMode.PercentOutput, 0);
        S_Intake_Piston.set(false);
        
    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Intake.this) {
                    stop();
                    Motor_Intake_TSRX.setNeutralMode(NeutralMode.Brake);
                    if(Logging){startLogging();}
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Intake.this) {
                    
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
            //En este subsistema se debería crear un archivo
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
        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

}