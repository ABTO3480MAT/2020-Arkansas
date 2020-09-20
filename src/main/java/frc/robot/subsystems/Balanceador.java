/*
* Subsystema para controlar el drive del robot.
*/

/*package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.team3480.lib.control.PIDController;
import com.team3480.lib.drivers.LazySparkMax;
import com.team3480.lib.drivers.SparkMaxFactory;
import com.team3480.lib.util.ReflectingCSVWriter;

//import org.graalvm.compiler.nodes.EncodedGraph;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants;
import frc.robot.Constants.Balanceador_Constants;
import frc.robot.loops.ILooper;
import frc.robot.loops.Loop;

import frc.robot.Constants;

public class Balanceador extends AbtomatSubsystem {

    public static class PeriodicIO {
        // INPUTS

        // OUTPUTS

    }

    private static Balanceador mInstance; // instancia unica
    private boolean Logging = false;
    private PeriodicIO mPeriodicIO;
    private LazySparkMax Motor_Neo_Balanceador;
    private CANPIDController PID_Balanceador;
    private CANEncoder Balanceador_Encoder;
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 m_colorSensor;
    private final ColorMatch m_colorMatcher;
    private final Color kBlueTarget;
    private final Color kGreenTarget;
    private final Color kRedTarget;
    private final Color kYellowTarget;
    private ReflectingCSVWriter<PeriodicIO> mCSVWriter = null;
    private double actual_pos = 0;
    private double setpoint_pos = 0;
    

    public synchronized static Balanceador getInstance() { // para leer la instancia unica
        if (mInstance == null) {
            mInstance = new Balanceador();
        }
        return mInstance;
    }

    private Balanceador() {
        mPeriodicIO = new PeriodicIO();
        //Motor_Neo_Balanceador= SparkMaxFactory.createDefaultSparkMax(Constants.Balanceador_Constants.kid_motor_Balanceador);
        m_colorSensor = new ColorSensorV3(i2cPort);
        m_colorMatcher = new ColorMatch();
        kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
        kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
        kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
        kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
        //Balanceador_Encoder = Motor_Neo_Balanceador.getEncoder();
        
        /*PID_Balanceador.setP(Constants.Balanceador_Constants.kP);
        PID_Balanceador.setI(Constants.Ruletero_Constants.kI);
        PID_Balanceador.setD(Constants.Ruletero_Constants.kD);
        PID_Balanceador.setIZone(Constants.Ruletero_Constants.kIz);
        PID_Balanceador.setFF(Constants.Ruletero_Constants.kFF);
        PID_Balanceador.setOutputRange(Constants.Ruletero_Constants.kMinOutput, Constants.Ruletero_Constants.kMaxOutput);
        */
        
        

   /* }
    
    public void MoveBalanceador(double POV){

        if (POV == 0){
            setpoint_pos = actual_pos + 20;
            //System.out.println("Balanceador izquierda");
        }else if (POV == 180){
            setpoint_pos = actual_pos -20;
            //System.out.println("Balanceador derecha");
        } 

    }

    /*public void Girar_Ruleta_Derecha(){
        Poder_Balanceador=.25;
        //System.out.println("Ruletero girando a la derecha");
    }
    public void Girar_Ruleta_Izquierda(){
        Poder_Balanceador=.25;
        //System.out.println("Ruletero girando a la izquierda");
    }*/
    


    /*public void Girar_3_Vueltas(boolean Giro_Activado){
        if(Giro_Activado){
            //System.out.println("Dando 3 vueltas");
        //PID_Balanceador.setReference(Constants.Balanceador_Constants.kPosicion_3_Vueltas, ControlType.kPosition);
        }
    }

    public void Girar_a_Color(boolean Instruccion_Girar){
        if(Instruccion_Girar){
            //System.out.println("Girar_A_Color ");
            Color detectedColor = m_colorSensor.getColor();

            String colorString;
            ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

            if (match.color == kBlueTarget) {
                colorString = "Azul";
            } else if (match.color == kRedTarget) {
                colorString = "Rojo";
            } else if (match.color == kGreenTarget) {
                colorString = "Verde";
            } else if (match.color == kYellowTarget) {
                colorString = "Amarillo";
            } else {
                colorString = "Desconocido";
            }

            //SmartDashboard.putString("Color detectado", colorString);
            //System.out.println("Color detectado"+  colorString);
        }
    }*/

   /* @Override
    public synchronized void readPeriodicInputs() {
        Color detectedColor = m_colorSensor.getColor();
        actual_pos = Balanceador_Encoder.getPosition();
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        //Motor_Neo_Balanceador.set(Poder_Balanceador);
        PID_Balanceador.setReference(setpoint_pos, ControlType.kPosition);
    }

    @Override
    public void zeroSensors() {
        setpoint_pos = 0;
        actual_pos = 0;
        Balanceador_Encoder.setPosition(0);
    }

    @Override
    public synchronized void stop() {
        Motor_Neo_Balanceador.set(ControlType.kDutyCycle, 0);
    }

    @Override
    public void registerEnabledLoops(ILooper in) {
        in.register(new Loop() {

            @Override
            public void onStart(double timestamp) {
                synchronized (Balanceador.this) {
                    stop();
                    //Motor_Neo_Balanceador.setIdleMode(IdleMode.kBrake);
                    m_colorMatcher.addColorMatch(kBlueTarget);
                    m_colorMatcher.addColorMatch(kGreenTarget);
                    m_colorMatcher.addColorMatch(kRedTarget);
                    m_colorMatcher.addColorMatch(kYellowTarget);
                    if (Logging) {
                        startLogging();
                    }
                }
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Balanceador.this) {
                    
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
            //Por el momento no necesitamos guardar estos valores
            //mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/TEST-LOGS.csv", PeriodicIO.class);
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
        if ( Logging && mCSVWriter != null) {
            mCSVWriter.write();
        }
    }

}*/