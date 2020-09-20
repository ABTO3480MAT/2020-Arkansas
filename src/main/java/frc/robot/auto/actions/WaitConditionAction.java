package frc.robot.auto.actions;

/*
///LBCHECK3480
*/

/**
 * Run a Lamnda function as an action
 */

public class WaitConditionAction implements Action {

    public interface BoolInterace { //Java Functional Interfaces
        boolean f();
    }

    BoolInterace mF;

    public WaitConditionAction(BoolInterace f) {
        this.mF = f;
    }

    @Override
    public void start() { }

    @Override
    public void update() {}

    @Override
    public boolean isFinished() {
        return mF.f();
    }

    @Override
    public void done() {}
}