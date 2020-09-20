package frc.robot.auto;

/*
///LBCHECK3480
*/

import frc.robot.auto.modes.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Optional;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

//import org.graalvm.compiler.core.common.type.ArithmeticOpTable.Op;

public class AutoModeSelector {

    enum DesiredMode {
        DO_NOTHING,
        TEST_PATH,
        Derecha_Adelante,
        Derecha_Atras,
        Izquierda_Adelante,
        Izquierda_Atras,
        Ocho_Derecha,
        Ocho_Pelotas,
        Ocho_Pelotas_V2,
    }

    private SendableChooser<DesiredMode> mModeChooser;

    private DesiredMode mCachedDesiredMode = null;
    private Optional<AutoModeBase> mAutoMode = Optional.empty();

    public AutoModeSelector() {
        mModeChooser = new SendableChooser<>();
        mModeChooser.setDefaultOption("hacer nada", DesiredMode.DO_NOTHING);
        mModeChooser.addOption("test path", DesiredMode.TEST_PATH);
        mModeChooser.addOption("Derecha Adelante", DesiredMode.Derecha_Adelante);
        mModeChooser.addOption("Derecha Atras", DesiredMode.Derecha_Atras);
        mModeChooser.addOption("Izquierda Adelante", DesiredMode.Izquierda_Adelante);
        mModeChooser.addOption("Izquierda Atras", DesiredMode.Izquierda_Atras);
        mModeChooser.addOption("Ocho Derecha", DesiredMode.Ocho_Derecha);
        mModeChooser.addOption("Ocho Pelotas", DesiredMode.Ocho_Pelotas);
        mModeChooser.addOption("Ocho Pelotas V2", DesiredMode.Ocho_Pelotas_V2);
        SmartDashboard.putData("Auto mode", mModeChooser);
    }

    public void updateModeCreator() {
        DesiredMode desiredMode = mModeChooser.getSelected();
        if (mCachedDesiredMode != desiredMode) {
            System.out.println("Auto selection changed, updating creator: desiredMode->" + desiredMode.name() );
            mAutoMode = getAutoModeForParams(desiredMode);
        }
        mCachedDesiredMode = desiredMode;
    }

    private Optional<AutoModeBase> getAutoModeForParams(DesiredMode mode) {
        switch (mode) {
            case DO_NOTHING:
                return Optional.of(new DoNothingMode());
            case TEST_PATH:
                return Optional.of(new TestControlFlowMode());
            case Derecha_Adelante:
                return Optional.of(new DerechaAdelante());
            case Derecha_Atras:
                return Optional.of(new DerechaAtras());
            case Izquierda_Adelante:
                return Optional.of(new IzquierdaAdelante());
            case Izquierda_Atras:
                return Optional.of(new IzquierdaAtras());
            /*case Ocho_Derecha:
                return Optional.of(new OchoDerecha());*/
            case Ocho_Pelotas:
                return Optional.of(new OchoPelotas());
            case Ocho_Pelotas_V2:
                return Optional.of(new OchoPelotasV2());
            default:
                break;
        }
        System.err.println("No valid auto mode found for  " + mode);
        return Optional.empty();
    }

    public void reset() {
        mAutoMode = Optional.empty();
        mCachedDesiredMode = null;
    }

    public void outputToSmartDashboard() {
        //SmartDashboard.putString("AutoModeSelected", mCachedDesiredMode.name());
    }

    public Optional<AutoModeBase> getAutoMode() {
        return mAutoMode;
    }

}
