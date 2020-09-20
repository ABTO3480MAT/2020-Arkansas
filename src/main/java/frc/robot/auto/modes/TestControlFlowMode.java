package frc.robot.auto.modes;

/*
///ABCHECK3480
*/

import frc.robot.auto.AutoModeEndedException;
import frc.robot.auto.actions.*;
import frc.robot.paths.Test.TestPath;
import frc.robot.paths.Test.TestPath2;
import frc.robot.paths.Test.TestPath3;
import frc.robot.subsystems.Drive;

public class TestControlFlowMode extends AutoModeBase {

    DrivePathAction path_1;
    DrivePathAction path_2;
    DrivePathAction path_3;

    public TestControlFlowMode() {
        path_1 = new DrivePathAction(new TestPath(),false);
        path_2 = new DrivePathAction(new TestPath2(),false);
        path_3 = new DrivePathAction(new TestPath3(),false);
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("***** Starting test control flow mode");
        runAction(new LambdaAction(() -> Drive.getInstance().UpdateDriveSpeed(0.3, 0.3)));
        runAction(new WaitAction(2));
        runAction(new LambdaAction(() -> Drive.getInstance().UpdateDriveSpeed(0, 0)));
        runAction(new LambdaAction(() -> Drive.getInstance().stop()));
        System.out.println("***** done - first wait action ");
        //runAction(path_2);
        //runAction(path_3);
    }

    @Override
    protected void endroutine() throws AutoModeEndedException {
        //detiene los subsitemas
    }

    /* Ejemplos de usar todos los metodos:
    * runAction(new DrivePathAction(new TestPath(),true));
    * runAction(new DriveOpenLoopAction(-0.15, -0.15, 0.75));
    
    * runAction(new ParallelAction(Arrays.asList(path_1, new SeriesAction(Arrays.asList(
                new WaitForPathMarkerAction("marker1"),
                new LambdaAction(() -> RobotState.getInstance().resetVision()),
                new LambdaAction(() -> Superstructure.getInstance().resetAimingParameters()),
                new WaitConditionAction(() -> { if(true){return true;}else{return false;} }), new ForceEndPathAction())))));
    * runAction(new WaitAction(10));
    */

}