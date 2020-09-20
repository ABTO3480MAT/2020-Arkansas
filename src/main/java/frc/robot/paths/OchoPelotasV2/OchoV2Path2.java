package frc.robot.paths.OchoPelotasV2;

import java.util.ArrayList;
import java.util.List;

import com.team3480.lib.control.Path;
import com.team3480.lib.control.PathInterface;
import com.team3480.lib.control.PathSegment;
import com.team3480.lib.control.Vector2;

/*
// Path de prueba.
//Notas: Los waypoints estan en x,y donde x positivo es a la dercha del robot visto desde atras y
// y es positivo hacia el frente del robot visto desde atras( y^R>x)
// Hay que tratar de que los paths terminen en recta siempre para que la rampa de frenado no sea tan brusca.
*/

public class OchoV2Path2 extends Path implements PathInterface{

    public OchoV2Path2() {
        RestartPath();
        pathSegment.isReversed=false;
        //pathSegment.MaxVelAlLimiter=0.8f; //para limitar la velocidad y aceleracion maxima
        //pathSegment.PointsSpacing=0.15; //en metros
        //pathSegment.WeightRealPoints=0.93; //peso de los puntos reales contra los smooth
        //pathSegment.SmoothTolerance=0.1;  //smooth tolerance para el smoother
        //pathSegment.CurvatureVelocityLimiter=2; //que tan rapido queremos que vaya en las curvas(1-5) 
        //pathSegment.LookAheadDistance=0.5; // en metros

        List<DataHolder> waypoints = new ArrayList<DataHolder>();
        waypoints.add(new DataHolder(new Vector2(0,0),"")); //x,y
        waypoints.add(new DataHolder(new Vector2(-0.25,0.75),""));
        waypoints.add(new DataHolder(new Vector2(-0.10,2.75),""));
        waypoints.add(new DataHolder(new Vector2(0.75,3.25),""));
        buildPath(waypoints);
        //System.out.println(pathSegment.GetCSV()); //debug del path info
    }

    @Override
    public PathSegment GetPath(){
        return(pathSegment);
    }

}
