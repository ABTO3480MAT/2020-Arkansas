package com.team3480.lib.control;

import java.util.ArrayList;
import java.util.List;

import com.team3480.lib.control.PathSegment;
import com.team3480.lib.control.Markers;


/**
 * Base de un path.
 *
 */

public abstract class Path {

    protected PathSegment pathSegment=null;

    protected class DataHolder{
        Vector2 position;
        String marker;
        public DataHolder(Vector2 position,String marker){
            this.position=position;
            this.marker=marker;
        }
    }

    public void RestartPath(){  //para inicializar el path
        pathSegment = new PathSegment();
    }

    public void buildPath(List<DataHolder> dataall) {  //genera el pathsegments
        List<Vector2> path = new ArrayList<Vector2>();
        List<Markers> markers = new ArrayList<Markers>();
        for (DataHolder data : dataall) {
            path.add(data.position);
            markers.add(new Markers(data.marker,0));
        }
        if(pathSegment==null){
            pathSegment = new PathSegment();
        }
        pathSegment.Generate(path, markers);
    }

}