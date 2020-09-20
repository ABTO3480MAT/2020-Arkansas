package com.team3480.lib.control;

/*
* Clase para generar un vector2 que se usa en el pathplanning
*/

public class Vector2 {

    public double x= 0.0;
    public double y= 0.0;

    public Vector2(double x, double y){
        this.x= x;
        this.y= y;
    }

    public void Multiply(double value){
        this.x*= value;
        this.y*= value;
    }

    public void Add(Vector2 value){
        this.x+= value.x;
        this.y+= value.y;
    }
}