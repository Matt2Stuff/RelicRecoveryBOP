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
package UserControlled;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import DriveEngine.JennyNavigation;
import Systems.JennyO1APickAndExtend;
import Systems.JennyV2PickAndExtend;

@TeleOp(name="Jenny O1A User Controlled", group="Linear Opmode")  // @Autonomous(...) is the other common choice
//@Disabled
public class JennyO1A extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    JennyNavigation navigation;
    JoystickHandler leftJoystick, rightJoystick;
    JennyO1APickAndExtend glyphSystem;

    @Override
    public void runOpMode() {
        try {
            navigation = new JennyNavigation(hardwareMap,"RobotConfig/JennyV2.json");
        }
        catch (Exception e){
            Log.e("Error!" , "Jenny Navigation: " + e.toString());
            throw new RuntimeException("Navigation Creation Error! " + e.toString());

        }
        try{
            glyphSystem = new JennyO1APickAndExtend(hardwareMap);

        }
        catch(Exception e){
            Log.e("Error!","Jenny Wheel Picks: " + e.toString());
            throw new RuntimeException("Navigation Creation Error! " + e.toString());
        }

        leftJoystick = new JoystickHandler(gamepad1,JoystickHandler.LEFT_JOYSTICK);
        rightJoystick = new JoystickHandler(gamepad1,JoystickHandler.RIGHT_JOYSTICK);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //DRIVE
            if(rightJoystick.magnitude() < .1){
                navigation.driveOnHeading(leftJoystick.angle(), leftJoystick.magnitude() * 50);
            }
            else navigation.turn(rightJoystick.magnitude() * rightJoystick.x()/Math.abs(rightJoystick.x()));

            //GLYPH GRABBER
            if(gamepad1.a) glyphSystem.grab();
            if(gamepad1.b) glyphSystem.spit();
            if(!gamepad1.a && !gamepad1.b) glyphSystem.pauseGrabber();

            //GLYPH LIFT
            if(gamepad1.right_trigger > 0.1) glyphSystem.lift();
            if(gamepad1.left_trigger > 0.1) glyphSystem.drop();
            if(gamepad2.right_trigger > 0.1 && gamepad1.right_trigger < 0.1 && gamepad1.left_trigger < 0.1 && !gamepad1.right_bumper && !gamepad1.left_bumper) glyphSystem.lift();
            if(gamepad2.left_trigger > 0.1 && gamepad1.right_trigger < 0.1 && gamepad1.left_trigger < 0.1 && !gamepad1.right_bumper && !gamepad1.left_bumper) glyphSystem.drop();
            if(gamepad1.right_trigger < 0.1 && gamepad1.left_trigger < 0.1 && gamepad2.left_trigger < 0.1 && gamepad2.right_trigger < 0.1) glyphSystem.pauseLift();
            //GLYPH ROLLER
            if(gamepad1.right_bumper) glyphSystem.startGlyphBelt();
            if(gamepad1.left_bumper) glyphSystem.reverseGlyphBelt();
            if(gamepad2.right_bumper && gamepad1.right_trigger < 0.1 && gamepad1.left_trigger < 0.1 && !gamepad1.right_bumper && !gamepad1.left_bumper) glyphSystem.startGlyphBelt();
            if(gamepad2.left_bumper && gamepad1.right_trigger < 0.1 && gamepad1.left_trigger < 0.1 && !gamepad1.right_bumper && !gamepad1.left_bumper) glyphSystem.reverseGlyphBelt();
            if(!gamepad1.right_bumper && !gamepad1.left_bumper && !gamepad2.right_bumper && !gamepad2.left_bumper) glyphSystem.pauseBelt();

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
        navigation.stop();
        glyphSystem.stop();
    }
}