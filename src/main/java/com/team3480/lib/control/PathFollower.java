package com.team3480.lib.control;

import java.util.ArrayList;
import java.util.List;

import com.team3480.lib.util.Utils;

import frc.robot.subsystems.Odometry;

public class PathFollower {
    
    public int LastPointUsed=0; //el ultimo punto que utilizo para encontrar el closest point
    public double LastlookAheadUsed= -1.0; //el ultimo punto porcentual
    public Vector2 lastlookAheadPoint=null; //para guadar el ultimo look ahead point

    public int GetClosestPoint(List<Vector2> path){ //busca el punto del path mas cercano al robot
        int nearestPoint= LastPointUsed;
        double distancemin= Double.MAX_VALUE;
        for(int i=LastPointUsed; i<path.size(); i++){
            double dist = Utils.Distance(path.get(i), Odometry.getInstance().mPeriodicIO.pose.position);
            if(dist < distancemin){
                nearestPoint= i;
                distancemin= dist;
            }
        }
        if(LastPointUsed != nearestPoint){
            LastPointUsed= nearestPoint;
            LastlookAheadUsed= -1.0; //restea el porcentaje lookahead point
        }
        return(LastPointUsed);
    }

    public Vector2 GetLookAheadPoint(List<Vector2> path, double lookAheadDistance){ //trae el punto que esta a la distancia look ahead del robot
       
        for(int i=LastPointUsed; i<(path.size()-1); i++){
            //Debug.Log("From: "+path[i].x+","+path[i].y+" To: "+path[i+1].x+","+path[i+1].y);
            Vector2 E= path.get(i); // the starting point of the line segment 
            Vector2 L= path.get(i+1); // the end point of the line segment 
            Vector2 C= Odometry.getInstance().mPeriodicIO.pose.position; //(robot location) 
            double r= lookAheadDistance; //(lookahead distance) 
            
            Vector2 d= new Vector2(L.x-E.x,L.y-E.y); // (Direction vector of ray, from start to end) 
            Vector2 f= new Vector2(E.x-C.x,E.y-C.y); //(Vector from center sphere to ray start) 
 
            double a= Utils.DotProduct(d,d);
            double b= 2*Utils.DotProduct(f,d);
            double c= Utils.DotProduct(f,f) - (r*r);
            double discriminant= (b*b)-(4.0*a*c);
            double intersection= -1.0;
            if( discriminant < 0.0 ) {
                // no intersection
            }else {
                // ray didn't totally miss sphere,
                // so there is a solution to
                // the equation.
                discriminant = Math.sqrt( discriminant );
                // either solution may be on or off the ray so need to test both
                // t1 is always the smaller value, because BOTH discriminant and
                // a are nonnegative.
                double t1 = (-b - discriminant)/(2.0*a);
                double t2 = (-b + discriminant)/(2.0*a);
                //Debug.Log("t1: "+t1);
                //Debug.Log("t2: "+t2);
                // 3x HIT cases:
                //          -o->             --|-->  |            |  --|->
                // Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit), 
 
                // 3x MISS cases:
                //       ->  o                     o ->              | -> |
                // FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)
                if( t1 >= 0.0 && t1 <= 1.0 ) { //intersection
                    // t1 is the intersection, and it's closer than t2
                    // (since t1 uses -b - discriminant)
                    // Impale, Poke
                    intersection= t1;
 
                // here t1 didn't intersect so we are either started
                // inside the sphere or completely past it
                }else if( t2 >= 0.0 && t2 <= 1.0 ) { //intersection
                    // ExitWound
                    intersection=t2;
                }
                // no intersection
            }
 
            if(intersection>=0.0 && intersection<=1.0 && intersection>LastlookAheadUsed){
                LastlookAheadUsed= intersection;
                d.Multiply(intersection);
                lastlookAheadPoint= new Vector2((E.x+d.x),(E.y+d.y));
                return (lastlookAheadPoint);  //intersection point
            }
        }
        return lastlookAheadPoint;
    }

    public double CurvatureArc(Vector2 lookaheadpoint){  //curvatura(arc) del robot al lookaheadpoint
        double L= Utils.Distance(Odometry.getInstance().mPeriodicIO.pose.position, lookaheadpoint);
        double a= Math.tan(Odometry.getInstance().mPeriodicIO.pose.rotation*(Math.PI/180))* -1.0;
        
        double b= 1.0;
        double c= (Math.tan(Odometry.getInstance().mPeriodicIO.pose.rotation*(Math.PI/180))*Odometry.getInstance().mPeriodicIO.pose.position.x)-Odometry.getInstance().mPeriodicIO.pose.position.y;

        double x= Math.abs((a*lookaheadpoint.x)+(b*lookaheadpoint.y)+c)/(Math.sqrt((a*a)+(b*b)));
        //Debug.Log("LookAhead x: "+x);

        double sign= 1.0; //para poner el signo a la curvatura
        double signednum = (Math.sin(Odometry.getInstance().mPeriodicIO.pose.rotation*(Math.PI/180))*(lookaheadpoint.x-Odometry.getInstance().mPeriodicIO.pose.position.x)) -
                          (Math.cos(Odometry.getInstance().mPeriodicIO.pose.rotation*(Math.PI/180))*(lookaheadpoint.y-Odometry.getInstance().mPeriodicIO.pose.position.y));
        if(signednum < 0.0){
            sign= -1.0;
        }
        double curvature = ((2.0*x)/(L*L))*sign;
        return(curvature);
    }

    public List<Double> CalculateVelocity(double targetvelocity, double arccurvature_lookahead, double trackwidth, double maxvelocity){  //para calcular la velocidad de cada lado
        List<Double> values = new ArrayList<Double>();
        values.add((targetvelocity*(2.0+(arccurvature_lookahead*trackwidth)))/2.0);  //left
        values.add((targetvelocity*(2.0-(arccurvature_lookahead*trackwidth)))/2.0); //right

        if(Math.abs(values.get(0))>Math.abs(maxvelocity) || Math.abs(values.get(1))>Math.abs(maxvelocity)){ //se escala proporcional si alguno se pasa del limite
            if( Math.abs(values.get(0)) > Math.abs(values.get(1)) ){
                double ratio= Math.abs(maxvelocity/values.get(0));
                values.set(0, maxvelocity);
                if(values.get(0)<0.0){
                    values.set(0, values.get(0)*-1.0);
                }
                values.set(1, values.get(1)*ratio);
            }else{
                double ratio= Math.abs(maxvelocity/values.get(1));
                values.set(1, maxvelocity);
                if(values.get(1)<0.0){
                    values.set(1, values.get(1)*-1.0);
                }
                values.set(0, values.get(0)*ratio);
            }
        }
        return(values);
    }
    
}