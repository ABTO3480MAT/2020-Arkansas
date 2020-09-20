package com.team3480.lib.control;

import java.util.List;

public class PIDController {
  
    public double kp = 0.0;
    public double ki = 0.0;
    public double kd = 0.0;
    public double epsilon = 0.0;
    public double lastpoint = 0.0;
    public double integralsum = 0.0;
    public double integrallimit = 0.1;
    public double outlimitPositive = 1.0;
    public double outlimitNegative = -1.0;
    public boolean dynamic = false;
    public double dynamic_mult = 0.0;
    public double dynamic_limit = 0.0;
    public double Izone=0;
  

    public PIDController(double kp, double ki, double kd, double epsilon){ //constructor
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.epsilon = epsilon;
    }

    public void MakeDynamic(boolean stater, double multiplier, double limit){ //para hacer el pid dinamico
        if(stater){
            dynamic = true;
            dynamic_mult = multiplier;
            dynamic_limit = limit;
        }else{
            dynamic = false;
            dynamic_mult = 0;
            dynamic_limit = 0;
        }
    }

    public double Get(double setpoint, double actualpoint, double dt, double feedforward){ //calcula el pid (actualpoint debe variar entre positivos y negativos)
        double output_val = 0.0;  //la salida
        
        //obtiene el error
        double error = setpoint - actualpoint;
        if(Math.abs(error) <= epsilon){
            error = 0.0;
            integralsum = 0.0;
            lastpoint = 0.0;
            return(feedforward);
        }
  
        ///calcula la integral considerando rectangulos pequenos donde dt es lo ancho y el error lo largo
        double temp_integral_val= integralsum;  //el area al graficar la variable contra tiempo(y se suma con lo que ya hay)
        if(Math.abs(error)<=Izone){
          temp_integral_val= integralsum + (error*dt*ki);
        
        ////para limitar la integral
        if(temp_integral_val>0.0 && temp_integral_val>integrallimit){
            temp_integral_val= integrallimit;
        }else if(temp_integral_val<0.0 && temp_integral_val<(-integrallimit)){
            temp_integral_val= (integrallimit*-1.0);
        }
        integralsum = temp_integral_val;
        } else {
          integralsum = 0;
          temp_integral_val = integralsum;
        }
        //calcula la derivada
        double temp_derivative_val = (actualpoint - lastpoint)/dt;  //la funcion dx/dt donde dx es la diferencia entre el ultimo error y el nuevo
  
        if(dynamic){
          if(Math.abs(error)>dynamic_limit){
            output_val = (kp*error)*dynamic_mult + (temp_integral_val) - (kd*temp_derivative_val)*dynamic_mult + feedforward;
          }else{
            output_val = (kp*error) + (temp_integral_val) - (kd*temp_derivative_val) + feedforward;
          }
        }else{
          output_val = (kp*error) + (temp_integral_val) - (kd*temp_derivative_val) + feedforward;
        }
  
        //Saturation filter, para asegurar que no pase los valores maximos ni minimos
        //sirve como una rampa tambien para evitar cambios bruscos
        if(output_val > outlimitPositive) output_val=outlimitPositive;
        if(output_val < (outlimitNegative)) output_val=(outlimitNegative);

        //guardamos el ultimo punto
        lastpoint = actualpoint;
  
        return(output_val); ///regresa el rewsultado del pid
      }
}