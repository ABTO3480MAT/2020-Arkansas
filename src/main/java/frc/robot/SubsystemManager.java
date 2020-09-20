package frc.robot;

import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;
import frc.robot.loops.Looper;
import frc.robot.subsystems.AbtomatSubsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Used to reset, start, stop, and update all subsystems at once
 */

public class SubsystemManager implements ILooper {

    private class EnabledLoop implements Loop { //clase de enable
        @Override
        public void onStart(double timestamp) {
            mLoops.forEach(l -> l.onStart(timestamp));
        }

        @Override
        public void onLoop(double timestamp) {
            mAllSubsystems.forEach(AbtomatSubsystem::readPeriodicInputs);
            mLoops.forEach(l -> l.onLoop(timestamp));
            mAllSubsystems.forEach(AbtomatSubsystem::writePeriodicOutputs);
        }

        @Override
        public void onStop(double timestamp) {
            mLoops.forEach(l -> l.onStop(timestamp));
        }
    }

    private class DisabledLoop implements Loop { //clase de disable
        @Override
        public void onStart(double timestamp) {}

        @Override
        public void onLoop(double timestamp) {
            mAllSubsystems.forEach(AbtomatSubsystem::readPeriodicInputs);
        }

        @Override
        public void onStop(double timestamp) {}
    }


    public static SubsystemManager mInstance = null; //instanc ia unica

    private List<AbtomatSubsystem> mAllSubsystems; //lista para guardar todos los subsistemas
    private List<Loop> mLoops = new ArrayList<>();

    private SubsystemManager() {} //constructor

    public static SubsystemManager getInstance() { //genera la instancia unica o la lee
        if (mInstance == null) {
            mInstance = new SubsystemManager();
        }
        return mInstance;
    }

    public void outputToSmartDashboard() { //para llamar el que hace el debug de cada subsytema
        mAllSubsystems.forEach(AbtomatSubsystem::outputTelemetry);
    }

    public boolean checkSubsystems() { //llama la funcion de chequeo de cada subsitema
        boolean ret_val = true;
        for (AbtomatSubsystem s : mAllSubsystems) {
            ret_val &= s.checkSystem();
        }
        return ret_val;
    }

    public void stop() { //llama la funcion stop de los subsitemas
        mAllSubsystems.forEach(AbtomatSubsystem::stop); 
    }

    public List<AbtomatSubsystem> getSubsystems() { ///lee la lista de subsistemas
        return mAllSubsystems;
    }

    public void setSubsystems(AbtomatSubsystem... allSubsystems) { //agrega los subsytemas a la lista
        mAllSubsystems = Arrays.asList(allSubsystems);
    }

    public void registerEnabledLoops(Looper enabledLooper) {
        mAllSubsystems.forEach(s -> s.registerEnabledLoops(this)); //envia este ILooper(para usar el register de esta) a cada subsystema
        enabledLooper.register(new EnabledLoop());
    }

    public void registerDisabledLoops(Looper disabledLooper) {
        disabledLooper.register(new DisabledLoop());
    }

    @Override
    public void register(Loop loop) { //para registrar cada subsytema a este looper
        mLoops.add(loop);
    }
}
