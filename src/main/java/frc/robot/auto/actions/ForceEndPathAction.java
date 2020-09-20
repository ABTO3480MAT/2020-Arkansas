package frc.robot.auto.actions;

/*
///ABCHECK3480
*/

import frc.robot.subsystems.Drive;

public class ForceEndPathAction extends RunOnceAction { //forza terminar un path

    @Override
    public synchronized void runOnce() {
        Drive.getInstance().EndPath(true);
    }
}