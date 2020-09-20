
package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    //##Looper ##//
    public static final double kLooperDt = 0.01; //seconds
    //##########//

    //##Motors Drive ##//
    public static final double kDriveGearRatio = 6; //gear ratio del chasis  15
    public static final int kMaxMotorRPMS = 5874; //rpms de los motores del chasis
    public static final double kMaxAngularSpeed = ((kMaxMotorRPMS/60)*2*Math.PI)/kDriveGearRatio; //velocidad angular de los motores del chasis
    //##########//

    //#Wheels#//
    public static final double kDriveWheelTrackWidth = 0.673;  //en metros
    public static final double kDriveWheelDiameter = 0.1524; //en metros
    public static final double kDriveWheelRadius = kDriveWheelDiameter / 2.0; //en metros 2
    //################//

    //#DRIVE CONSTANTS#//
    public static final double kDriveEncoderPPR = (128*1.4);//128
    public static final boolean kInvertNavx=false; //positivo a la izquierda rotacion
    public static final boolean kInvertMotorsOutput=true;
    public static final double kDriveVoltageRampRate = 0; 
    //##########//

    //#####PID Drive###########//
    public static final boolean kDriveSpeedPIDActive=false;
    public static final double kDriveSpeed_kp = 0.1; 
    public static final double kDriveSpeed_ki = 0.0; 
    public static final double kDriveSpeed_kd = 0.001; 
    public static final double kDriveSpeed_epsilon = 0.0; 
    public static final double kDriveSpeed_kff = 1; //feedforward 
    //#########################//

    //#PathPlanning Constants#//
    public static final double kMaxVoltage = 9;  // 2 max voltage to calculate max speed and acceleration
    public static final double kKV= 3.9624;  //v m/s  calculado con el robot characterization tool
    public static final double kKA = 2.746;  // v m/s2 calculado con el robot characterization tool
    public static final double kMaxVelocity = kMaxVoltage/kKV;  //metros/segundo
    public static final double kMaxAcceleration = kMaxVoltage/kKA; //metros/segundo^2

    public static final double kPointsSpacing = 0.15; // en metros    
    public static final double kWeightRealPoints = 0.983; //peso de los puntos reales contra los smooth
    public static final double kSmoothTolerance = 0.01; //smooth tolerance
    public static final double kCurvatureVelocityLimiter = 2; //que tan rapido queremos que vaya en las curvas(1-5) 
    public static final double kLookAheadDistance = 0.5; // en metros

    public static final double kDriveRampPathFollow = 0.1; //rampa en el pathfollow
    public static final double kDriveRamp = 0.1; //rampa en el drive manejado
    //################//
    


    ///////////DRIVE///////////
    public static final int kid_Motor_Right_Master=4;
    public static final int kid_Motor_Right_Slave=3;
    public static final int kid_Motor_Left_Master=1;
    public static final int kid_Motor_Left_Slave=2;
    ///////////////////////////

    //////////LIMELIGHT////////
    public static final double kAlturaTarget = 2.5; // metros
    public static final double kAlturaCamara = 0.65; // metros
    public static final double kCamaraAnguloVertical = 10.5 ; //23.0; // metros
    public static final double kTargetInner= 3.28;
    ///////////////////////////


    /////////INTAKE////////////
    public static final int kid_Motor_Intake=14;
    public static final int kid_Piston_Intake=0;
    public static final int kIR_1=1;
    public static final int kIR_2=2;
    public static final int kIR_3=3;
    ///////////////////////////


    //////////BALANCEADOR//////////////
    public static final class Balanceador_Constants{
        public static final int kid_motor_Balanceador=25;

        public static final double kPosicion_3_Vueltas=0;

        //Constantes del PID
        public static final double kP = 0; 
        public static final double kI = 0;
        public static final double kD = 0; 
        public static final double kIz = 0; 
        public static final double kFF = 0; 
        public static final double kMaxOutput = 1; 
        public static final double kMinOutput = -1;
    }
    ////////////////////////////


    //////ELEVADOR DE PELOTAS///////
    public static final int kid_Motor_Elevador_Pelotas=13;
    ////////////////////////////////


    /////////////TORRETA Y////////////////
    public static final class Torreta_Y_Constants{
        public static final int kid_Motor_Torreta=8;

        //Constantes del PID
        public static final double kP = 0.07; 
        public static final double kI = 0;
        public static final double kD = 0; 
        public static final double kIz = 0; 
        public static final double kFF = 0.0001; 
        public static final double kMaxOutput = 1; 
        public static final double kMinOutput = -1;
    }
    /////////////////////////////


    ///////TORRETA X/////////////
    public static final class Torreta_X_Constants{
        public static final int kid_Motor_Torreta=9;

        //Constantes del PID
        public static final double kP = .05; 
        public static final double kI = 0;
        public static final double kD = 0; 
        public static final double kIz = 0; 
        public static final double kFF = 0.0001; 
        public static final double kMaxOutput = 1; 
        public static final double kMinOutput = -1;
    }
    /////////////////////////////


    ///////CLIMBER///////////////
    public static final class Climber_Constants{
        public static final int kid_Motor_Climber_Izquierdo=5;
        public static final int kid_Motor_Climber_Derecho=6;

        //Constantes del PID
        public static final double kP = .05; 
        public static final double kI = 0;
        public static final double kD = 0; 
        public static final double kIz = 0; 
        public static final double kFF = 0.0001; 
        public static final double kMaxOutput = 1; 
        public static final double kMinOutput = -1;
        //Motion magic constants
        public static final double kmaxRPM=0;
        public static final double kmaxVel=0;
        public static final double kminVel=0;
        public static final double kmaxAcc=0;
        public static final double kallowedErr=0;

        //posisciones
        public static final double kBalanceador_pos = 100;
        public static final double kRuleta_pos = 25;
    }
    ///////////////////////////


    //////////SHOOTER//////////
    public static final int kid_Motor_Shooter_Izquierdo=11;
    public static final int kid_Motor_Shooter_Derecho=12;
    ///////////////////////////

    /////TRANSPORTADOR/////////
    public static final int kid_Motor_Transportador=10;
    ///////////////////////////


    ///////CONTROL IDs/////////
    public static final class Control_Xbox_Constants{

        //Todos los Sticks y Triggers (Analogos)

        public static final int kStick_Left_Xaxis=0;
        public static final int kStick_Left_Yaxis=1;
        public static final int kTrigger_Left=2;
        public static final int kTrigger_Right=3;
        public static final int kStick_Right_Xaxis=4; 
        public static final int kStick_Right_Yaxis=5;

        //Todos los botones del mando (booleanos)

        public static final int kButton_A=1;
        public static final int kButton_B=2;
        public static final int kButton_X=3;
        public static final int kButton_Y=4;
        public static final int kButton_LB=5;
        public static final int kButton_RB=6;
        public static final int kButton_Back=8;
        public static final int kButton_Start=7;
        public static final int kButton_Click_Left=9;
        public static final int kButton_Click_Right=10;
   }

}
