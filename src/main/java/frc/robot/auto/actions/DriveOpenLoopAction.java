package frc.robot.auto.actions;

/*
// Clase para hacer que el robot se mueva por cierto tiempo.
*/

import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Drive.DriveMode;
import edu.wpi.first.wpilibj.Timer;

public class DriveOpenLoopAction implements Action { //path follow or drivers
    private static final Drive mDrive = Drive.getInstance();

    private double mStartTime;
    private final double mDuration, mLeft, mRight;

    public DriveOpenLoopAction(double left, double right, double duration) {
        mDuration = duration;
        mLeft = left;
        mRight = right;
    }

    @Override
    public void start() {
        mDrive.UpdateOpenLoopModeSpeed(mLeft, mRight);
        mDrive.driveMode = DriveMode.OpenLoopMode;
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public void update() {}

    @Override
    public boolean isFinished() {
        return ((Timer.getFPGATimestamp() - mStartTime) > mDuration);
    }

    @Override
    public void done() {
        mDrive.UpdateOpenLoopModeSpeed(0,0);
        mDrive.driveMode = DriveMode.Drivers;
        mDrive.stop();
    }
}