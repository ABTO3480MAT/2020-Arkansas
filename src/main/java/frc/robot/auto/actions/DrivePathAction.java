package frc.robot.auto.actions;

import com.team3480.lib.control.PathInterface;

/*
///ABCHECK3480
*/

import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Drive.DriveMode;

/**
 * Drives the robot along the Path defined in the PathContainer object. The action finishes once the robot reaches the
 * end of the path.
 *
 * @see PathContainer
 * @see Path
 * @see Action
 */

public class DrivePathAction implements Action {

    private PathInterface mPath;
    private Drive mDrive = Drive.getInstance();
    private boolean mStopWhenDone;

    public DrivePathAction(PathInterface path, boolean stopWhenDone) {
        mPath = path;
        mStopWhenDone = stopWhenDone;
    }

    @Override
    public void start() {
        mDrive.SetPath(mPath.GetPath());
        mDrive.driveMode = DriveMode.PathFollow;
    }

    @Override
    public void update() {}

    @Override
    public boolean isFinished() {
        return mDrive.isDoneWithPath();
    }

    @Override
    public void done() {
        mDrive.driveMode = DriveMode.Drivers;
        if (mStopWhenDone) {
            mDrive.stop();
        }
    }
}
