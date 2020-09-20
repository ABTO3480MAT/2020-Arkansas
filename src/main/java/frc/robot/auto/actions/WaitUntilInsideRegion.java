package frc.robot.auto.actions;

import com.team3480.lib.control.Vector2;

import frc.robot.subsystems.Odometry;

/*
///ABCHECK3480
*/


public class WaitUntilInsideRegion implements Action {

    private final static Odometry modometry = Odometry.getInstance();

    private final Vector2 mBottomLeft;
    private final Vector2 mTopRight;

    public WaitUntilInsideRegion(Vector2 bottomLeft, Vector2 topRight) {
        mBottomLeft = bottomLeft;
        mTopRight = topRight;
    }

    @Override
    public boolean isFinished() {
        Vector2 position = modometry.mPeriodicIO.pose.position;
        return position.x > mBottomLeft.x && position.x < mTopRight.x
                && position.y > mBottomLeft.y && position.y < mTopRight.y;
    }

    @Override
    public void update() {}

    @Override
    public void done() {}

    @Override
    public void start() {}
}
