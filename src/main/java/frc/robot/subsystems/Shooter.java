/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team3480.lib.drivers.LazySparkMax;
import com.team3480.lib.drivers.SparkMaxFactory;
import com.team3480.lib.util.ReflectingCSVWriter;

import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Shooter extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Shooter mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private LazySparkMax Motor_Neo_Shooter_Master , Motor_Neo_Shooter_Slave;
    private TalonSRX Motor_derecho, Motor_izquierdo;
    private double Distancia_Shooter;
    private double Poder_Shooter;
    private boolean Shooter_Ready;
    public static boolean Activar_Elevador_Pelotas;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Shooter getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Shooter();
        }
        return mInstance;
    }

    private Shooter() {
        mPeriodicIO = new PeriodicIO();
        // Motor_Neo_Shooter_Master= SparkMaxFactory.createDefaultSparkMax(Constants.Shooter_Constants.kid_Motor_Shooter_Front);
        //Motor_Neo_Shooter_Slave= SparkMaxFactory.createDefaultSparkMax(Constants.Shooter_Constants.kid_Motor_Shooter_Back);
        Motor_derecho = new TalonSRX(Constants.kid_Motor_Shooter_Derecho);
        Motor_izquierdo = new TalonSRX(Constants.kid_Motor_Shooter_Izquierdo);
    }

    public synchronized void Disparar (double Disparar){
        
        if(Disparar>.2){
        //System.out.println("Disparar");
            Poder_Shooter=1;
            Shooter_Ready=true;
        }else{
            Poder_Shooter=0;
            Shooter_Ready=false;
        }

    }

    public boolean getShooter_Ready(){
        return Shooter_Ready;
    }

    @Override
    public synchronized void readPeriodicInputs() {

    }

    @Override
    public synchronized void writePeriodicOutputs() {
        //Motor_Neo_Shooter_Master.set(-Poder_Shooter);
        //Motor_Neo_Shooter_Slave.set(Poder_Shooter);
        Motor_izquierdo.set(ControlMode.PercentOutput, Poder_Shooter);
        Motor_derecho.set(ControlMode.PercentOutput, -Poder_Shooter);
        //System.out.println(Poder_Shooter);

    }

    @Override
    public void zeroSensors() {
        Shooter_Ready=false;

    }

    @Override
    public synchronized void stop() {
        Poder_Shooter=0;
        Motor_izquierdo.set(ControlMode.PercentOutput, 0);
        Motor_derecho.set(ControlMode.PercentOutput, 0);
        Shooter_Ready=false;

    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Shooter.this) {
                    stop();
                    //Motor_Neo_Shooter_Master.setIdleMode(IdleMode.kBrake);
                    //Motor_Neo_Shooter_Slave.setIdleMode(IdleMode.kBrake);
                    Motor_derecho.setNeutralMode(NeutralMode.Brake);
                    Motor_derecho.setNeutralMode(NeutralMode.Brake);
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Shooter.this) {
                    
                    if(Logging && mCSVWriter != null){mCSVWriter.add(mPeriodicIO);}
                }
            }

            @Override
            public void onStop(double timestamp) {
                stop();
                //Motor_Neo_Shooter_Master.setIdleMode(IdleMode.kCoast);
                //Motor_Neo_Shooter_Slave.setIdleMode(IdleMode.kCoast);
                if(Logging){stopLogging();}
            }
        });
    }

    public synchronized void startLogging() {
        if (mCSVWriter == null) {
            //En este subsistema se debe crear un archivo para guardar los valores
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