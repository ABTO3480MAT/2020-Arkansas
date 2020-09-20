/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.team3480.lib.util.ReflectingCSVWriter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team3480.lib.drivers.*;

public class Transportador extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Transportador mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private LazySparkMax Motor_Neo_Transportador;
    private double Poder_Transportador;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;
    private boolean Estado_Tragando, Estado_Elevador=false;

    public synchronized static Transportador getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Transportador();
        }
        return mInstance;
    }

    

    private Transportador() {
        mPeriodicIO = new PeriodicIO();
        Motor_Neo_Transportador=SparkMaxFactory.createDefaultSparkMax(Constants.kid_Motor_Transportador);
       

    }

    public synchronized void Move_Transportador (boolean Estado_boton){
        //System.out.println("Transportando");
        if (Estado_Tragando||Estado_Elevador||Estado_boton){
            Poder_Transportador = -.5;
        } else {
            Poder_Transportador = 0;
        }
    }

    @Override
    public synchronized void readPeriodicInputs() {
        Estado_Tragando = Intake.getInstance().GetTragandoEstado();
        Estado_Elevador = Elevador_Pelotas.getInstance().getSubiendoEstado();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        Motor_Neo_Transportador.set(ControlType.kDutyCycle, Poder_Transportador);
    }

    @Override
    public void zeroSensors() {
        Estado_Tragando=false;
        Estado_Elevador=false;

    }

    @Override
    public synchronized void stop() {

        Poder_Transportador=0;
        Motor_Neo_Transportador.set(ControlType.kDutyCycle, 0);

    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Transportador.this) {
                    stop();
                    Motor_Neo_Transportador.setIdleMode(IdleMode.kBrake);
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Transportador.this) {
                    
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