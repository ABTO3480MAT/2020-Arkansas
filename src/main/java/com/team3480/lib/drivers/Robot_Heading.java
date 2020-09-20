package com.team3480.lib.drivers;

import com.kauailabs.navx.frc.AHRS;
import com.team3480.lib.util.CrashTracker;

import edu.wpi.first.wpilibj.SPI;

/*
///LBCHECK3480
*/

/*
* Clase para el control del navx
*/

public class Robot_Heading{

	public enum DataType{Rotation,Roll,Pitch,FusedHeading}

    private static Robot_Heading mInstance; //instancia unica de la clase

    public synchronized static Robot_Heading getInstance() {
        if (mInstance == null) {
            mInstance = new Robot_Heading();
        }
  
        return mInstance;
	}

	private AHRS ahrs;
	private double rotationsave;
	private double pitchsave;
	private double rollsave;
	private double fusedheadingsave;
	
	public Robot_Heading() { //constructor
		try {
			CrashTracker.logMarker("Navx Init...");
			ahrs = new AHRS(SPI.Port.kMXP);
			Thread.sleep(500); //millis
			ahrs.reset();
			Thread.sleep(500); //millis
			rotationsave=ahrs.getAngle();
			rollsave=ahrs.getRoll();
			pitchsave=ahrs.getPitch();
			fusedheadingsave=ahrs.getFusedHeading(); //calibrar el magnetometro para que jale mejor!!!
		}
		catch(Exception e){
			CrashTracker.logThrowableCrash(e);
		}
	}
	
	public void resetRotation() { //resetea la rotacion
		rotationsave = ahrs.getAngle();
	}

	public void resetRoll() { //resetea el roll
		rollsave = ahrs.getRoll();
	}

	public void resetPitch() { ////resetea el pitch
		pitchsave = ahrs.getPitch();
	}

	public void resetFusedHeading(){ //reseta el head fusion heading
		fusedheadingsave=ahrs.getFusedHeading();
	}

	public double getRawRotation(){ //para regresar la data de rotacion raw
		return ahrs.getAngle();
	}
	public double getRotation() { //regresa la rotacion limitada
		return mapAngle(ahrs.getAngle(),DataType.Rotation);
	}

	public double getRawRoll(){  //para regresar la data de roll raw
		return ahrs.getRoll();
	}
	public double getRoll(){ //regresa el roll limitada
		return mapAngle(ahrs.getRoll(),DataType.Roll);
	}

	public double getRawPitch(){  //para regresar la data de pitch raw
		return ahrs.getPitch();
	}
	public double getPitch(){ //regresa el pitch limitada
		return mapAngle(ahrs.getPitch(),DataType.Pitch);
	}

	public double getRawFusedHeading(){  //para regresar la data de pitch raw
		return ahrs.getFusedHeading();
	}
	public double getFusedHeading(){ //regresa el pitch limitada
		return mapAngle(ahrs.getFusedHeading(),DataType.FusedHeading);
	}
	
	public double mapAngle(double angle,DataType typex){ //da los grados limitados de 180 a -180 y considerando el reset de variable
		if(typex==DataType.Rotation){
			angle -= rotationsave;
		}else if(typex==DataType.Roll){
			angle -= rollsave;
		}else if(typex==DataType.Pitch){
			angle -= pitchsave;
		}else if(typex==DataType.FusedHeading){
			angle -= fusedheadingsave;
		}
		////operacion modulo que jala con negativos////
		int rs=(int)(angle/360);
		double valx = angle-(rs*360);
		/////////////////////////////////////////////
		if(valx>180){
			valx = valx-360;
		}
		if(valx<-180){
			valx = valx+360;
		}
		return valx;
    }
	
}
