package frc.robot.auto.modes;

/*
///ABCHECK3480
*/

/*import frc.robot.auto.AutoModeEndedException;
import frc.robot.auto.actions.DrivePathAction;
import frc.robot.paths.OchoDerecha.OchoDerechaPath;
import frc.robot.paths.OchoDerecha.OchoDerechaPath2;
import frc.robot.paths.OchoDerecha.OchoDerechaPath3;
import frc.robot.paths.OchoDerecha.OchoDerechaPath4;

public class OchoDerecha extends AutoModeBase {

    DrivePathAction path_1;
    DrivePathAction path_2;
    DrivePathAction path_3;
    DrivePathAction path_4;
    DrivePathAction path_5;

    public OchoDerecha() {
        path_1 = new DrivePathAction(new OchoDerechaPath(),true);
        path_2 = new DrivePathAction(new OchoDerechaPath2(),true);
        path_3 = new DrivePathAction(new OchoDerechaPath3(),true);
        path_4 = new DrivePathAction(new OchoDerechaPath4(),true);
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("***** Starting test control flow mode");
        runAction(path_1);
        System.out.println("***** done - first wait action ");
        runAction(path_2);
        runAction(path_3);
        runAction(path_4);
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

//}