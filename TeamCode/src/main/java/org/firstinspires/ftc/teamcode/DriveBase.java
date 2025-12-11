/* Copyright (c) 2021 FIRST. All rights reserved.
 * ... (copyright header) ... */

package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class DriveBase extends LinearOpMode {

    // Declare OpMode members
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx intakeMotor = null;
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;

    private DcMotorEx LauncherLeft = null;
    private DcMotorEx LauncherRight = null;


    private Servo RStandardServo = null;   // This is for a 180-degree servo
    private Servo LStandardServo = null;   // This is for a 180-degree servo


    @Override
    public void runOpMode() {

        // --- INITIALIZATION ---
        // Initialize all motors from the hardware map.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

        // Initialize the intake motor and set its mode for velocity control.
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        LauncherLeft = hardwareMap.get(DcMotorEx.class, "LauncherLeft" );
        LauncherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        LauncherRight = hardwareMap.get(DcMotorEx.class, "LauncherRight" );
        LauncherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RStandardServo = hardwareMap.get(Servo.class, "RStandard_Servo");
        LStandardServo = hardwareMap.get(Servo.class, "LStandard_Servo");




        // Set motor directions.
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
        LauncherLeft.setDirection(DcMotor.Direction.REVERSE);
        LauncherRight.setDirection(DcMotor.Direction.FORWARD);
        RStandardServo.setDirection(Servo.Direction.REVERSE);
        LStandardServo.setDirection(Servo.Direction.REVERSE);





        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // --- WAIT FOR START ---
        waitForStart();
        runtime.reset();

        // --- TELEOP LOOP ---
        // This single loop runs until the match ends.
        while (opModeIsActive()) {

            // --- MECANUM DRIVE LOGIC ---
            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            double frontLeftPower = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower = axial - lateral + yaw;
            double backRightPower = axial + lateral - yaw;

            // Normalize wheel powers.
            double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower /= max;
                frontRightPower /= max;
                backLeftPower /= max;
                backRightPower /= max;
            }

            // Send power to drive wheels.
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            // --- INTAKE MOTOR LOGIC ---
            // This is the code that controls your intake based on the Y and X buttons.
            if (gamepad1.y) {
                // When Y is pressed, run intake forward.
                intakeMotor.setVelocity(280);
                RStandardServo.setPosition(-1.0);
                LStandardServo.setPosition(-1.0);


            } else if (gamepad1.x) {
                // When X is pressed, run intake backward.
                intakeMotor.setVelocity(-310);

            } else {
                // If neither Y nor X is pressed, stop the intake.
                intakeMotor.setVelocity(0);
            }

            if (gamepad1.b) {
                //When B is pressed turn on both motors.
                LauncherRight.setVelocity(-312);
                LauncherLeft.setVelocity(-312);

            } else {
                //When nothing is pressed, don't run launcher.
                LauncherRight.setVelocity(0);
                LauncherLeft.setVelocity(0);

            }

            // --- TELEMETRY ---
            // Display data on the Driver Station phone.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.addData("Intake Velocity", intakeMotor.getVelocity());
            telemetry.update();
        }
    }
}
