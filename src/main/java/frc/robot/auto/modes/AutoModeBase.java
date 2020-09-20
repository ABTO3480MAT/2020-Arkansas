package frc.robot.auto.modes;

/*
///LBCHECK3480
*/

import frc.robot.auto.AutoModeEndedException;
import frc.robot.auto.actions.Action;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * An abstract class that is the basis of the robot's autonomous routines. This is implemented in auto modes (which are
 * routines that do actions).
 */
public abstract class AutoModeBase {

    protected final double mUpdateRate = 1.0 / 50.0; //update rate del autonomo
    protected boolean mActive = false;
    protected boolean mIsInterrupted = false;

    protected abstract void routine() throws AutoModeEndedException;
    protected abstract void endroutine() throws AutoModeEndedException;

    public void run() {
        mActive = true;

        try {
            routine();
        } catch (AutoModeEndedException e) {
            DriverStation.reportError("AUTO MODE DONE!!!! ENDED EARLY!!!!", false);
            return;
        }

        done();
    }

    public void done() {
        System.out.println("Auto mode done");
        try {
            endroutine();
        } catch (AutoModeEndedException e) {
            DriverStation.reportError("Automode Done Neded Wrong", false);
        }
    }

    public void stop() {
        mActive = false;
        try {
            endroutine();
        } catch (AutoModeEndedException e) {
            DriverStation.reportError("Automode Done Neded Wrong", false);
        }
    }

    public boolean isActive() {
        return mActive;
    }

    public boolean isActiveWithThrow() throws AutoModeEndedException {
        if (!isActive()) {
            throw new AutoModeEndedException();
        }

        return isActive();
    }

    public void interrupt() {
        System.out.println("** Auto mode interrrupted!");
        mIsInterrupted = true;
    }

    public void resume() {
        System.out.println("** Auto mode resumed!");
        mIsInterrupted = false;
    }

    public void runAction(Action action) throws AutoModeEndedException { //funcion para correr las acciones
        isActiveWithThrow();
        long waitTime = (long) (mUpdateRate * 1000.0);

        // Wait for interrupt state to clear
        while (isActiveWithThrow() && mIsInterrupted) {
            try {
                Thread.sleep(waitTime); //millis
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        action.start();

        // Run action, stop action on interrupt, non active mode, or done
        while (isActiveWithThrow() && !action.isFinished() && !mIsInterrupted) {
            action.update();

            try {
                Thread.sleep(waitTime); //millis
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        action.done();

    }

    public boolean getIsInterrupted() {
        return mIsInterrupted;
    }
}