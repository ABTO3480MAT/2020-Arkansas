package com.team3480.lib.control;

import com.team3480.lib.util.CSVWritable;

/*
* Clase de un pose (position/rotation) para usar en el path planning
*/

public class Pose implements CSVWritable{
    public Vector2 position= new Vector2(0.0, 0.0);
    public double rotation= 0.0;


    @Override
    public String toCSV(){
        String data = position.x+","+position.y+","+rotation ;
        return(data);
    };

    @Override
    public String toCSVHeader(){
        String data = "posX,posY,Rotation";
        return(data);
    };
}
