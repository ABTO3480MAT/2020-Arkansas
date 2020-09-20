package frc.robot.auto.modes;

/*
///LBCHECK3480
*/

import frc.robot.auto.AutoModeEndedException;

//autonomo que no hace nada

public class DoNothingMode extends AutoModeBase {
    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("doing nothing");
    }

    @Override
    protected void endroutine() throws AutoModeEndedException {
        //detiene los subsitemas
    }
}



    /* Ejemplos de usar todos los metodos:
    * runAction(new WaitConditionAction(() -> { if(true){return true;}else{return false;} }));
      runAction(new LambdaAction(() -> RobotState.getInstance().resetVision());


      
    * runAction(new ParallelAction(Arrays.asList( new LambdaAction(() -> RobotState.getInstance().resetVision());
        new SeriesAction(Arrays.asList(
                new LambdaAction(() -> RobotState.getInstance().resetVision()),
                new LambdaAction(() -> Superstructure.getInstance().resetAimingParameters()),
                new WaitUntilSeesTargetAction())))));
    * runAction(new WaitAction(10));
    */