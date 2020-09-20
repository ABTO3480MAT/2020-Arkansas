/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class RobotContainer {

  public XboxController Xbox_Chasis, Xbox_Subsistemas ;

  public RobotContainer() {
    // Configure the button bindings
    Xbox_Chasis = new XboxController(1);
    Xbox_Subsistemas = new XboxController(0);

    configureButtonBindings();

  }



  private void configureButtonBindings() {

  }

}
