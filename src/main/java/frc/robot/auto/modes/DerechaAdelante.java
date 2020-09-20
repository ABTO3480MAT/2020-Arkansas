package frc.robot.auto.modes;

import edu.wpi.first.wpilibj.Timer;

/*
///ABCHECK3480
*/

import frc.robot.auto.AutoModeEndedException;
import frc.robot.auto.actions.DrivePathAction;
import frc.robot.paths.DerechaAdelante.DerechaAdelantePath;
import frc.robot.paths.DerechaAdelante.DerechaAdelantePath2;
import frc.robot.subsystems.*;
import frc.robot.auto.actions.*;

public class DerechaAdelante extends AutoModeBase {

    DrivePathAction path_1;
    DrivePathAction path_2;

    public DerechaAdelante() {
        path_1 = new DrivePathAction(new DerechaAdelantePath(),false);
        path_2 = new DrivePathAction(new DerechaAdelantePath2(),false);
    }

    @Override 
    protected void routine() throws AutoModeEndedException {
        double starttimer = Timer.getFPGATimestamp();
        System.out.println("***** Starting Derecha Adelante");
        runAction(new LambdaAction(() -> Drive.getInstance().ResetErrorPathFlag() ));
        runAction(path_1);
        runAction(new WaitConditionAction(() -> { if (!Drive.getInstance().GetErrorPathFlag()){return true;} else {return false;} }));
        System.out.println("***** done - first wait action ");
        runAction(path_2);
        runAction(new WaitConditionAction(() -> { if (!Drive.getInstance().GetErrorPathFlag()){return true;} else {return false;} }));

        System.out.println("Done auto in " + (Timer.getFPGATimestamp()-starttimer));
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