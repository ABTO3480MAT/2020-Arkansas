/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.team3480.lib.util.ReflectingCSVWriter;

import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

public class testSubsystem extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS


        // OUTPUTS
        
    }

    private static testSubsystem mInstance;  //instancia unica 
    private boolean Logging=false;
    private PeriodicIO mPeriodicIO;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static testSubsystem getInstance() { //para leer la instancia unica 
        if (mInstance == null) {
            mInstance = new testSubsystem();
        }
        return mInstance;
    }

    private testSubsystem() {
        mPeriodicIO = new PeriodicIO();

    }


    @Override
    public synchronized void readPeriodicInputs() {
        
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
                synchronized (testSubsystem.this) {
                    stop();
                    if(Logging){startLogging();}
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (testSubsystem.this) {
                    
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
            mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/TEST-LOGS.csv", PeriodicIO.class);
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