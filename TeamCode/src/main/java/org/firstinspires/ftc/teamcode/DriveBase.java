/* Copyright (c) 2021 FIRST. All rights reserved.
 * ... (copyright header) ... */

package org.firstinspires.ftc.teamcode;import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
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

    @Override
    public void runOpMode() {

        // --- INITIALIZATION ---
        // Initialize all motors from the hardware map.
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Initialize the intake motor and set its mode for velocity control.
        intakeMotor = hardwareMap.get(DcMotorEx.class, "31626 Intake");
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set motor directions.
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotor.Direction.REVERSE);

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
                intakeMotor.setVelocity(312);
            } else if (gamepad1.x) {
                // When X is pressed, run intake backward.
                intakeMotor.setVelocity(-310);
            } else {
                // If neither Y nor X is pressed, stop the intake.
                intakeMotor.setVelocity(0);
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
