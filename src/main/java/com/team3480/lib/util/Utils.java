package com.team3480.lib.util;

import com.team3480.lib.control.Vector2;

/*
///LBCHECK3480
*/

public class Utils {
    public static double Magnitude(Vector2 vector){ //magnitud de un vector
        return Math.sqrt( (vector.x*vector.x)+(vector.y*vector.y) );
    }
    public static Vector2 Normalize(Vector2 vector){ //vector normalizado
        double magnitude = Magnitude(vector);
        return (new Vector2((vector.x/magnitude),(vector.y/magnitude)));
    }
    public static double Distance(Vector2 vector1,Vector2 vector2){ //la magnitud de la diferencia entre 2 vectores
        Vector2 difpoint = new Vector2(vector2.x-vector1.x, vector2.y-vector1.y);
        return(Magnitude(difpoint)); 
    }
    public static double DotProduct(Vector2 vector1,Vector2 vector2){ //producto punto de un vector
        return((vector1.x*vector2.x)+(vector1.y*vector2.y));
    }
    public static double map(double x, double in_min, double in_max, double out_min, double out_max) { //para mapear de un rango a otro
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
