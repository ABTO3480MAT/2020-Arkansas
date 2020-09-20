package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

/*
///LBCHECK3480
*/

/**
 * Run a Lamnda function as an action by certain time
 */

public class DoActionWithTime implements Action {

    public interface VoidInterace { //Java Functional Interfaces
        void f();
    }

    VoidInterace mF;
    private final double mTimeToWait;
    private double mStartTime;

    public DoActionWithTime(VoidInterace f,double timeToWait) {
        this.mF = f;
        mTimeToWait = timeToWait;
    }

    @Override
    public void start() {
        mStartTime = Timer.getFPGATimestamp();
     }

    @Override
    public void update() {
        mF.f();
    }

    @Override
    public boolean isFinished() {
        return (Timer.getFPGATimestamp() - mStartTime) >= mTimeToWait;
    }

    @Override
    public void done() {}
}