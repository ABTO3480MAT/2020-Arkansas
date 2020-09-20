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

import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class Climber extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Climber mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private LazySparkMax Motor_Neo_Izquierdo, Motor_Neo_Derecho;
    private double Poder_Climb;
    private CANEncoder Climber_Encoder;
    private CANPIDController PID_Climber;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;
    private double setpoint_pos=0;
    private double actual_pos=0;
    private double max_pos = -700;

    public synchronized static Climber getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Climber();
        }
        return mInstance;
    }

    private Climber() {
        mPeriodicIO = new PeriodicIO();
        Motor_Neo_Izquierdo= SparkMaxFactory.createDefaultSparkMax(Constants.Climber_Constants.kid_Motor_Climber_Izquierdo);
        Motor_Neo_Derecho= SparkMaxFactory.createPermanentSlaveSparkMax(Constants.Climber_Constants.kid_Motor_Climber_Derecho, Motor_Neo_Izquierdo, true);
        Motor_Neo_Izquierdo.setIdleMode(IdleMode.kBrake);
        Motor_Neo_Derecho.setIdleMode(IdleMode.kBrake);

        Climber_Encoder = Motor_Neo_Izquierdo.getEncoder();
        Climber_Encoder.setPosition(0);
        PID_Climber = Motor_Neo_Izquierdo.getPIDController();
        
        PID_Climber.setP(Constants.Climber_Constants.kP);
        PID_Climber.setI(Constants.Climber_Constants.kI);
        PID_Climber.setD(Constants.Climber_Constants.kD);
        PID_Climber.setIZone(Constants.Climber_Constants.kIz);
        PID_Climber.setFF(Constants.Climber_Constants.kFF);
        PID_Climber.setOutputRange(Constants.Climber_Constants.kMinOutput, Constants.Climber_Constants.kMaxOutput);
        
    }

    public void Escalar (Boolean Button_Arriba, Boolean Button_Abajo){
        
        if (Button_Arriba){
            setpoint_pos = actual_pos + ( 1 * -25);
            //System.out.println("Subir elevador manual");
        } else if (Button_Abajo){
            setpoint_pos = actual_pos - ( 1 * -25);
        }
        if (setpoint_pos < max_pos){
            setpoint_pos = max_pos;
        }
        if (setpoint_pos > 0){
            setpoint_pos = 0;
        }
        //System.out.println("CLIMBERpos: "+actual_pos);
    }

    public void SetClimbPos(double newsetpoint){
        setpoint_pos = newsetpoint;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        actual_pos = Climber_Encoder.getPosition();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        PID_Climber.setReference(setpoint_pos, ControlType.kPosition);

    }

    @Override
    public void zeroSensors() {
        Climber_Encoder.setPosition(0);
        setpoint_pos = 0;
        actual_pos = 0;
    }

    @Override
    public synchronized void stop() {
        Motor_Neo_Izquierdo.set(ControlType.kDutyCycle, 0);
        Motor_Neo_Izquierdo.setIdleMode(IdleMode.kBrake);
        Motor_Neo_Derecho.setIdleMode(IdleMode.kBrake);

    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Climber.this) {
                    stop();
                    Motor_Neo_Izquierdo.setIdleMode(IdleMode.kBrake);
                    Motor_Neo_Derecho.setIdleMode(IdleMode.kBrake);
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Climber.this) {
                    
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