/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team3480.lib.util.ReflectingCSVWriter;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Elevador_Pelotas extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Elevador_Pelotas mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private TalonSRX Motor_Elevador_Pelotas;
    private double Poder_Elevador_P;
    private boolean Disparando=false, Shooter_Ready=false;
    private DigitalInput IR;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Elevador_Pelotas getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Elevador_Pelotas();
        }
        return mInstance;
    }

    private Elevador_Pelotas() {
        mPeriodicIO = new PeriodicIO();
        Motor_Elevador_Pelotas= new TalonSRX(Constants.kid_Motor_Elevador_Pelotas);
        //IR=new DigitalInput(5);

    }

    public synchronized void Transportar_Shooter (double Trigger){
        //System.out.println("Transportando al shooter");

        
        if(Trigger>.20 && Shooter_Ready){
            Poder_Elevador_P=-1; 
            Disparando=true;
        }else{
            Poder_Elevador_P=0;
            Disparando=false;
        }
        
    }

    public boolean getSubiendoEstado(){
        return Disparando;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        Shooter_Ready= Shooter.getInstance().getShooter_Ready();


    }

    @Override
    public synchronized void writePeriodicOutputs() {
        Motor_Elevador_Pelotas.set(ControlMode.PercentOutput, Poder_Elevador_P);
        //System.out.println(Poder_Elevador_P);
    }

    @Override
    public void zeroSensors() {
        Disparando=false;

    }

    @Override
    public synchronized void stop() {
        Poder_Elevador_P=0;
        Motor_Elevador_Pelotas.set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Elevador_Pelotas.this) {
                    stop();
                    Motor_Elevador_Pelotas.setNeutralMode(NeutralMode.Brake);
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Elevador_Pelotas.this) {
                    
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
            //No necesitamos valores de este subsistema
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