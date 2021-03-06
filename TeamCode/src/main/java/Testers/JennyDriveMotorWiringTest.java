/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package Testers;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import DriveEngine.JennyNavigation;

import static Autonomous.RelicRecoveryField.BLUE_ALLIANCE_2;
import static Autonomous.RelicRecoveryField.startLocations;

/*
    An opmode to test if all our drive wheels are working correctly
 */
@TeleOp(name="Jenny Drive Wiring Test", group="Linear Opmode")  // @Autonomous(...) is the other common choice
//@Disabled
public class JennyDriveMotorWiringTest extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    JennyNavigation navigation;
    @Override
    public void runOpMode() {
        double [] drivePowers = {0,0,0,0};
        try {
            navigation = new JennyNavigation(hardwareMap, startLocations[BLUE_ALLIANCE_2], 0, "RobotConfig/JennyV2.json");
        }
        catch (Exception e){
            Log.e("Error!" , "Jenny Navigation: " + e.toString());
            throw new RuntimeException("Navigation Creation Error! " + e.toString());

        }
        //leftJoystick = new JoystickHandler(gamepad1,JoystickHandler.LEFT_JOYSTICK);
        //rightJoystick = new JoystickHandler(gamepad1,JoystickHandler.RIGHT_JOYSTICK);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            drivePowers[0] = 0;
            drivePowers[1] = 0;
            drivePowers[2] = 0;
            drivePowers[3] = 0;
            if(gamepad1.a){
                drivePowers[JennyNavigation.BACK_LEFT_HOLONOMIC_DRIVE_MOTOR] = .5;
            }

            if(gamepad1.b){
                drivePowers[JennyNavigation.BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR] = .5;
            }
            if(gamepad1.x){
                drivePowers[JennyNavigation.FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR] = .5;
            }
            if(gamepad1.y){
                drivePowers[JennyNavigation.FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR] = .5;
            }
            navigation.applyMotorPowers(drivePowers);
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left tick", navigation.driveMotors[navigation.FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick());
            telemetry.addData("Front right tick", navigation.driveMotors[navigation.FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick());
            telemetry.addData("Back left tick", navigation.driveMotors[navigation.BACK_LEFT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick());
            telemetry.addData("Back right tick", navigation.driveMotors[navigation.BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick());
            telemetry.addData("Front left INCH", navigation.driveMotors[navigation.FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR].convertTicksToInches(navigation.driveMotors[navigation.FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick()));
            telemetry.addData("Front right INCH", navigation.driveMotors[navigation.FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR].convertTicksToInches(navigation.driveMotors[navigation.FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick()));
            telemetry.addData("Back left INCH", navigation.driveMotors[navigation.BACK_LEFT_HOLONOMIC_DRIVE_MOTOR].convertTicksToInches(navigation.driveMotors[navigation.BACK_LEFT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick()));
            telemetry.addData("Back right INCH", navigation.driveMotors[navigation.BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR].convertTicksToInches(navigation.driveMotors[navigation.BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR].getCurrentTick()));
            telemetry.update();
        }
        navigation.stopNavigation();
    }
}
