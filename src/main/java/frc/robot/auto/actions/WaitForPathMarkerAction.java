package frc.robot.auto.actions;

/*
///ABCHECK3480
*/

import frc.robot.subsystems.Drive;

public class WaitForPathMarkerAction implements Action { //espera a que llegue a un punto en el path

    private Drive mDrive = Drive.getInstance();
    private String mMarker;

    public WaitForPathMarkerAction(String marker) {
        mMarker = marker;
    }

    @Override
    public boolean isFinished() {
        return mDrive.hasPassedMarker(mMarker);
    }

    @Override
    public void update() {}

    @Override
    public void done() {}

    @Override
    public void start() {}
}