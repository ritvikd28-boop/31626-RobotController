/* Copyright (c) 2017 FIRST. All rights reserved.
 * ... (copyright header remains the same) ... */


package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous
public class Auto extends LinearOpMode {

        /* Declare OpMode members. */
        private DcMotor leftDrive = null;
        private DcMotor rightDrive = null;
        private ElapsedTime runtime = new ElapsedTime();

        // --- ROBOT CONSTANTS (Tune these for your robot) ---
        static final double COUNTS_PER_MOTOR_REV = 1440;    // Example: TETRIX Motor Encoder
        static final double DRIVE_GEAR_REDUCTION = 1.0;     // No external gearing.
        static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
        static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                (WHEEL_DIAMETER_INCHES * 3.1415);
        // --- MOVEMENT CONSTANTS ---
        static final double DRIVE_SPEED = 0.4;     // Slower speed for shorter distance

        @Override
        public void runOpMode() {

            // --- INITIALIZATION ---
            leftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
            rightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");

            leftDrive.setDirection(DcMotor.Direction.REVERSE);
            rightDrive.setDirection(DcMotor.Direction.FORWARD);

            leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            telemetry.addData("Status", "Ready to run");
            telemetry.update();

            // Wait for the game to start (driver presses START)
            waitForStart();

            // --- AUTONOMOUS PATH ---

            // Step 1: Drive forward for 2 inches
            telemetry.addData("Path", "Step 1: Driving forward 2 inches");
            telemetry.update();
            encoderDrive(DRIVE_SPEED, 2.0, 2.0, 4.0); // Drive 2 inches with a 4-second timeout

            // Step 2: Wait for half a second
            telemetry.addData("Path", "Step 2: Waiting 0.5 seconds");
            telemetry.update();
            sleep(500); // 500 milliseconds = 0.5 seconds

            // --- END OF PATH ---
            telemetry.addData("Path", "Complete");
            telemetry.update();
            sleep(1000);  // Keep "Complete" message on screen for 1 second
        }

        /**
         * Method to perform a relative move, based on encoder counts.
         */
        public void encoderDrive(double speed,
                                 double leftInches, double rightInches,
                                 double timeoutS) {
            int newLeftTarget;
            int newRightTarget;

            if (opModeIsActive()) {
                newLeftTarget = leftDrive.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
                newRightTarget = rightDrive.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
                leftDrive.setTargetPosition(newLeftTarget);
                rightDrive.setTargetPosition(newRightTarget);

                leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                runtime.reset();
                leftDrive.setPower(Math.abs(speed));
                rightDrive.setPower(Math.abs(speed));

                while (opModeIsActive() &&
                        (runtime.seconds() < timeoutS) &&
                        (leftDrive.isBusy() && rightDrive.isBusy())) {
                    // Wait until the move is complete
                    telemetry.addData("Running to", " %7d :%7d", newLeftTarget, newRightTarget);
                    telemetry.addData("Currently at", " at %7d :%7d",
                            leftDrive.getCurrentPosition(), rightDrive.getCurrentPosition());
                    telemetry.update();
                }

                leftDrive.setPower(0);
                rightDrive.setPower(0);

                leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
        }
    }
