/*
* Subsystema para controlar el drive del robot.
*/

package frc.robot.subsystems;

import com.ctre.phoenix.Util;
import com.team3480.lib.util.ReflectingCSVWriter;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Limelight mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    //public double tv, tx, ty, ta; //Valores de la network tv la detecta, tx horizontal , ty vertical, ta area del target
    //public String ledmode, camMode, pipeline, shapshot;
    //public double Potencia_Giro_X=.03;
    //public double Potencia_Giro_Y=.03;
    //public double kp=.0005;
    //public static double Distancia;
    //public boolean Detectado = false ;
    //public double ajuste, Error_X, Error_Y;
    //public static double Poder_Limelight_Eje_X, Poder_Limelight_Eje_Y;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;

    public synchronized static Limelight getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Limelight();
        }
        return mInstance;
    }

    private Limelight() {
        mPeriodicIO = new PeriodicIO();
      

    }
    
public double getTarget(){
    return NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
}
public double getAngulo_Controlado(){
    
    return Math.toDegrees(Math.atan((Constants.kTargetInner-Constants.kAlturaCamara)/getDistanciaCorrecta()));
}

public double getAngleX(){
    return NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
}

public double getAngleY(){
    return NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
}

public double getArea(){
    return NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);
}

public double getDistance(){
    return (Constants.kAlturaTarget-Constants.kAlturaCamara)/(Math.tan(Math.toRadians(getAngulo_Camara_Degrees()+getAngleY())));//Se le  esta sumando el angulo actual
}

public double getDistanciaCorrecta(){
    return ((getDistance()*1.43)-0.67);
}

public double getAngulo_Camara(){
    return Math.atan((Constants.kAlturaTarget-Constants.kAlturaCamara)/2.75) - Math.toRadians(Constants.kCamaraAnguloVertical);
}

public double getAngulo_Camara_Degrees(){
    return Math.toDegrees(getAngulo_Camara());
}

public double getAngulo_Disparador(){
    return Math.atan(Constants.kTargetInner/getDistanciaCorrecta());
}

/*
public double getDistance_Degrees(){
    return (Constants.Limelight.kAlturaTarget-Constants.Limelight.kAlturaCamara)/(Math.tan((Constants.Limelight.kCamaraAnguloVertical)+getAngleY()+Torreta_Y.getInstance().getAngulo_Motor_Y()));//Se le  esta sumando el angulo actual
}
*/
  

    @Override
    public synchronized void readPeriodicInputs() {

    }

    @Override
    public synchronized void writePeriodicOutputs() {

    }

    @Override
    public void zeroSensors() {

    }

    @Override
    public synchronized void stop() {

    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Limelight.this) {
                    stop();
                    
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Limelight.this) {
                    
                    if(Logging && mCSVWriter != null){mCSVWriter.add(mPeriodicIO);}
                }
            }

            @Override
            public void onStop(double timestamp) {

                stop();
                
                if(Logging){stopLogging();}
            }
        });
    }

    public synchronized void startLogging() {
        if (mCSVWriter == null) {
            mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/TEST-LOGS.csv", PeriodicIO.class);
        }
    }

    public synchronized void stopLogging() {
        if (mCSVWriter != null) {
            mCSVWriter.flush();
            mCSVWriter = null;
        }
    }

    @Override
    public boolean checkSystem() {
        return(true);
    }

    @Override
    public void outputTelemetry() {
        //System.out.println(getDistanciaCorrecta());
        SmartDashboard.putNumber("Distancia", getDistanciaCorrecta());
        SmartDashboard.putNumber("Distancia Pedorra", getDistance());
        SmartDashboard.putNumber("ANGULO CONTROLADO", getAngulo_Controlado());
        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

}