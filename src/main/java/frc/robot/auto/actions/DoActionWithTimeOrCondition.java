package frc.robot.auto.actions;

import edu.wpi.first.wpilibj.Timer;

/*
///LBCHECK3480
*/

/**
 * Run a Lamnda function as an action by certain time
 */

public class DoActionWithTimeOrCondition implements Action {

    public interface VoidInterace { //Java Functional Interfaces
        void f();
    }

    public interface BoolInterace { //Java Functional Interfaces
        boolean f();
    }

    VoidInterace mF;
    BoolInterace bF;
    private final double mTimeToWait;
    private double mStartTime;

    public DoActionWithTimeOrCondition(VoidInterace f,double timeToWait,BoolInterace condition) {
        this.mF = f;
        this.bF = condition;
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
        if(((Timer.getFPGATimestamp() - mStartTime) >= mTimeToWait) || bF.f()){
            return true;
        }
        return false;
    }

    @Override
    public void done() {}
}