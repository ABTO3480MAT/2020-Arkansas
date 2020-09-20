package frc.robot.auto.actions;

/*
///LBCHECK3480
*/

/**
 * Run a Lamnda function as an action
 */

public class LambdaAction implements Action {

    public interface VoidInterace { //Java Functional Interfaces
        void f();
    }

    VoidInterace mF;

    public LambdaAction(VoidInterace f) {
        this.mF = f;
    }

    @Override
    public void start() {
        mF.f();
    }

    @Override
    public void update() {}

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void done() {}
}