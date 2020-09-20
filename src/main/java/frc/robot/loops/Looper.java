package frc.robot.loops;

/*
///LBCHECK3480
*/

import frc.robot.Constants;
import com.team3480.lib.util.CrashTrackingRunnable;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * This code runs all of the robot's loops. Loop objects are stored in a List object. They are started when the robot
 * powers up and stopped after the match.
 */

public class Looper implements ILooper {

    public final double kPeriod = Constants.kLooperDt; //dt del loop (cada cuanto corre)

    private boolean mRunning;

    private final Notifier mNotifier;
    private final List<Loop> mLoops;
    private final Object mTaskRunningLock = new Object();
    private double mTimestamp = 0;
    private double mDT = 0;

    private final CrashTrackingRunnable runnable_ = new CrashTrackingRunnable() { //hacemos el runnable que va estar corriendo cada ciclo
        @Override
        public void runCrashTracked() {
            synchronized (mTaskRunningLock) {
                if (mRunning) {
                    double now = Timer.getFPGATimestamp();

                    for (Loop loop : mLoops) {
                        loop.onLoop(now);
                    }

                    mDT = now - mTimestamp;
                    mTimestamp = now;
                }
            }
        }
    };

    public Looper() { //constructor
        mNotifier = new Notifier(runnable_);
        mRunning = false;
        mLoops = new ArrayList<>();
    }

    @Override
    public synchronized void register(Loop loop) {  //agrega los loops a la lista
        synchronized (mTaskRunningLock) {
            mLoops.add(loop);
        }
    }

    public synchronized void start() { //inicia los loops
        if (!mRunning) {
            System.out.println("Starting loops");

            synchronized (mTaskRunningLock) {
                mTimestamp = Timer.getFPGATimestamp();
                for (Loop loop : mLoops) {
                    loop.onStart(mTimestamp);
                }
                mRunning = true;
            }

            mNotifier.startPeriodic(kPeriod);
        }
    }

    public synchronized void stop() { //detiene los loops
        if (mRunning) {
            System.out.println("Stopping loops");
            mNotifier.stop();

            synchronized (mTaskRunningLock) {
                mRunning = false;
                mTimestamp = Timer.getFPGATimestamp();
                for (Loop loop : mLoops) {
                    System.out.println("Stopping " + loop);
                    loop.onStop(mTimestamp);
                }
            }
        }
    }
    
}
