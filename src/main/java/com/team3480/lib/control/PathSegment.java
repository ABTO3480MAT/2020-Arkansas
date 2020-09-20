package com.team3480.lib.control;

import java.util.ArrayList;
import java.util.List;
import com.team3480.lib.util.Utils;
import frc.robot.Constants;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Odometry;

public class PathSegment {

    public static class PeriodicIO { }

    public List<Markers> markers; //para guardar los markers del path segment
    public List<Vector2> points; //para guardar los puntos del path segment
    public List<Double> curvatures; //para guardar los puntos del path segment
    public List<Double> velocities; //para guardar las velocidades

    public double MaxVelAlLimiter=1; //para limitar la velocidad y aceleracion maxima
    public double PointsSpacing=Constants.kPointsSpacing; //en metros
    public double WeightRealPoints=Constants.kWeightRealPoints; //peso de los puntos reales contra los smooth
    public double SmoothTolerance=Constants.kSmoothTolerance;  //smooth tolerance para el smoother
    public double CurvatureVelocityLimiter=Constants.kCurvatureVelocityLimiter; //que tan rapido queremos que vaya en las curvas(1-5) 
    public double LookAheadDistance=Constants.kLookAheadDistance; // en metros

    public boolean isReversed=false; //para invertir el movimiento del robot


    public void Generate(final List<Vector2> path, final List<Markers> markers){  //genera el path segment
        points = CalculatePoints(path,markers);
        points = Smoother(points,WeightRealPoints,(1-WeightRealPoints),SmoothTolerance);
        curvatures = GetCurvatures(points);
        velocities = GetVelocities(points,curvatures,Constants.kMaxVelocity*MaxVelAlLimiter,Constants.kMaxAcceleration*MaxVelAlLimiter);
    }

    public String GetCSV(){ //para regresar la info del path como csv
        int conterxx=0;
        String text="";
        for(Vector2 point: points){
        	text+=point.x+","+point.y+",0,"+curvatures.get(conterxx)+","+velocities.get(conterxx)+"\n";
            conterxx++;
        }
		return(text);
    }

    private List<Vector2> CalculatePoints(final List<Vector2> path, final List<Markers> passedmarkers){ //calcula los puntos intermedios entre los waypoints
        List<Vector2> newpath = new ArrayList<Vector2>();
        markers = new ArrayList<Markers>();
        for(int a=0; a<(path.size()-1); a++){
            markers.add(new Markers(passedmarkers.get(a).marker,newpath.size())); //agrega el nuevo index del marker
            Vector2 vector= new Vector2((path.get(a+1).x-path.get(a).x),(path.get(a+1).y-path.get(a).y));
            final int num_extra_points= (int)Math.ceil(Utils.Magnitude(vector)/PointsSpacing);
            vector= Utils.Normalize(vector);
            vector.Multiply(PointsSpacing);
            for(int b=0; b<num_extra_points; b++){
                newpath.add(new Vector2( (path.get(a).x+(vector.x*b)),(path.get(a).y+(vector.y*b)) ));
            }
        }
        newpath.add(path.get(path.size()-1));
        return(newpath);
    }

    private List<Vector2> Smoother(final List<Vector2> path, final double weight_data, final double weight_smooth, final double tolerance) { //para darle al curva al path
		List<Vector2> newPath= new ArrayList<Vector2>(path);
		double change= tolerance; //para guadar el cambio en entre los puntos que va cambiando
		while(change >= tolerance) {
			change= 0.0;
			for(int i=1; i<newPath.size()-1; i++){
                ///x
                double aux= newPath.get(i).x;
                newPath.get(i).x+=( weight_data * (path.get(i).x - newPath.get(i).x) + weight_smooth * (newPath.get(i-1).x + newPath.get(i+1).x - (2.0 * newPath.get(i).x)) );
                change += Math.abs(aux - newPath.get(i).x);
                ///y
                aux= newPath.get(i).y;
                newPath.get(i).y+=( weight_data * (path.get(i).y - newPath.get(i).y) + weight_smooth * (newPath.get(i-1).y + newPath.get(i+1).y - (2.0 * newPath.get(i).y)));
                change += Math.abs(aux - newPath.get(i).y);	
            }				
		}
		return(newPath);
    }
    
    /*
    private List<Double> GetDistancePoints(List<Vector2> path){  //para tener la distancia entre puntos
        List<Double> distances= new ArrayList<Double>();
        double distancesum= 0.0;
        for(int i=1; i<path.size(); i++){
            distancesum= distancesum + Utils.Distance(path.get(i-1), path.get(i));
            distances.add(distancesum);
        }
        return(distances);
    }
    */

    private List<Double> GetCurvatures(List<Vector2> path){ //saca la curvatura de cada punto(menos el primero y ultimo porque tienen que intersectar 3 puntos con un circulo)
        List<Double> curvatures= new ArrayList<Double>();
        curvatures.add(0.0); //primer punto
        for(int i=1; i<path.size()-1; i++){
            if(path.get(i).x == path.get(i-1).x){ path.get(i).x+= 0.001; }
            double k1 = (0.5*((path.get(i).x*path.get(i).x)+(path.get(i).y*path.get(i).y)-(path.get(i-1).x*path.get(i-1).x)-(path.get(i-1).y*path.get(i-1).y)))/(path.get(i).x-path.get(i-1).x);
            double k2 = (path.get(i).y-path.get(i-1).y)/(path.get(i).x-path.get(i-1).x);
            double b=(0.5*((path.get(i-1).x*path.get(i-1).x)-(2*path.get(i-1).x*k1)+(path.get(i-1).y*path.get(i-1).y)-(path.get(i+1).x*path.get(i+1).x)+
                    (2.0*path.get(i+1).x*k1)-(path.get(i+1).y*path.get(i+1).y)))/((path.get(i+1).x*k2)-path.get(i+1).y+path.get(i-1).y-(path.get(i-1).x*k2));
            double a= k1-(k2*b);
            double r = Math.sqrt(((path.get(i).x-a)*(path.get(i).x-a))+((path.get(i).y-b)*(path.get(i).y-b))); 
            if(Double.isNaN(1/r)){
                curvatures.add(0.0);
            }else{
                curvatures.add(1/r);
            }
        }
        curvatures.add(0.0); //ultimo punto
        return(curvatures);
    }

    private ArrayList<Double> GetVelocities(List<Vector2> path, List<Double> curvatures,double maxvelocity, double maxacceleration){ //saca la velocidad para cada punto
        double k= CurvatureVelocityLimiter;  //que tan rapido queremos que vaya en las curvas(1-5) 
        List<Double> velocities= new ArrayList<Double>();
        for(int i=0; i<path.size(); i++){
            velocities.add(0.0);
        }
        velocities.set(path.size()-1, 0.0);
        for(int i=path.size()-2; i>=0; i--){
            double distance = Utils.Distance(path.get(i), path.get(i+1));
            double tempvelocity = maxvelocity;
            if(curvatures.get(i)!=0){
                tempvelocity= Math.min(maxvelocity,(k/curvatures.get(i)));
            }
            velocities.set(i, Math.min(tempvelocity, Math.sqrt((velocities.get(i+1)*velocities.get(i+1))+(2.0*distance*maxacceleration))) );
        }
        return(new ArrayList<Double>(velocities));
    }

    public void Reverse(){ //para invertir el estado del robot
        if(!isReversed){
            Drive.getInstance().isPathFollowInverted=false;
            Drive.getInstance().resetEncoders();
            Odometry.getInstance().ResetDistances();
        }else{
            ///Para invertir:
            //isPathFollowInverted cambia la logica de la rampa de frenado del path folow y
            //suma 180 al navx y invirte lo que se pone a cada motor como pwm y por -1 y invierte los encoders y *-1
            Drive.getInstance().isPathFollowInverted=true;
            Drive.getInstance().resetEncoders();
            Odometry.getInstance().ResetDistances();
        }
    }

}