package frc.robot.auto.modes;

/*
///ABCHECK3480
*/

import frc.robot.auto.AutoModeEndedException;
import frc.robot.auto.actions.DrivePathAction;
import frc.robot.paths.IzquierdaAtras.IzquierdaAtrasPath;
import frc.robot.paths.IzquierdaAtras.IzquierdaAtrasPath2;

public class IzquierdaAtras extends AutoModeBase {

    DrivePathAction path_1;
    DrivePathAction path_2;

    public IzquierdaAtras() {
        path_1 = new DrivePathAction(new IzquierdaAtrasPath(),true);
        path_2 = new DrivePathAction(new IzquierdaAtrasPath2(),true);
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("***** Iniciando Izquierda Adelante");
        runAction(path_1);
        System.out.println("***** done - first wait action ");
        runAction(path_2);
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