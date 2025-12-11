package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Auto: Main Autonomous", group="Linear OPMODE")
public class Auto extends LinearOpMode {

    // Drive motors
    private DcMotorEx frontLeftDrive = null;
    private DcMotorEx backLeftDrive = null;
    private DcMotorEx frontRightDrive = null;
    private DcMotorEx backRightDrive = null;

    // Other mechanisms
    private DcMotorEx intakeMotor = null;
    private DcMotorEx launcherLeft = null;
    private DcMotorEx launcherRight = null;
    private Servo rStandardServo = null;
    private Servo lStandardServo = null;

    private ElapsedTime runtime = new ElapsedTime();

    // --- ROBOT CONSTANTS ---

    // These theoretical values are now only for reference.
    static final double COUNTS_PER_MOTOR_REV    = 537.7;
    static final double DRIVE_GEAR_REDUCTION    = 1.0;
    static final double WHEEL_DIAMETER_INCHES   = 4.0;

    // =================================================================================================
    // IMPORTANT: THIS IS THE NUMBER YOU MUST TUNE FOR YOUR ROBOT.
    // 1. Run the code and measure how far the robot ACTUALLY travels.
    // 2. Calculate the correct value with: New_Value = 42.79 * (10 / Actual_Distance_Measured)
    // 3. Replace the number below with your new, calculated value.
    //
    // (The initial value of 42.79 comes from the theoretical calculation)
    // =================================================================================================
    static final double COUNTS_PER_INCH = 42.79; // <--- !!! REPLACE THIS WITH YOUR TUNED VALUE !!!

    /*
     * This is the theoretical calculation. We comment it out in favor of the tuned value above.
     * static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
     *         (WHEEL_DIAMETER_INCHES * Math.PI);
     */

    static final double DRIVE_SPEED = 0.5;

    @Override
    public void runOpMode() {

        // --- INITIALIZATION ---
        frontLeftDrive  = hardwareMap.get(DcMotorEx.class, "frontLeftDrive");
        backLeftDrive   = hardwareMap.get(DcMotorEx.class, "backLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "frontRightDrive");
        backRightDrive  = hardwareMap.get(DcMotorEx.class, "backRightDrive");

        intakeMotor     = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        launcherLeft    = hardwareMap.get(DcMotorEx.class, "LauncherLeft");
        launcherRight   = hardwareMap.get(DcMotorEx.class, "LauncherRight");
        rStandardServo  = hardwareMap.get(Servo.class, "RStandard_Servo");
        lStandardServo  = hardwareMap.get(Servo.class, "LStandard_Servo");

        // Directions
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        launcherLeft.setDirection(DcMotor.Direction.FORWARD);
        launcherRight.setDirection(DcMotor.Direction.REVERSE);

        // Reset encoders for all motors
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set all motors to run using encoders
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialization Complete");
        telemetry.addData(">", "Press PLAY to start the autonomous path.");
        telemetry.update();

        // --- WAIT FOR START ---
        waitForStart();
        runtime.reset();

        // --- AUTONOMOUS ACTIONS ---
        telemetry.addData("Path", "Step 1: Driving forward 10 inches");
        telemetry.update();
        encoderDrive(DRIVE_SPEED, 10, 10, 10, 10, 5.0);

        // --- Step 2: Activate intake, servos, and launchers ---
        telemetry.addData("Path", "Step 2: Activating mechanisms");
        telemetry.update();

        intakeMotor.setVelocity(1000);
        launcherLeft.setVelocity(1500);
        launcherRight.setVelocity(1500);
        rStandardServo.setPosition(0.5);
        lStandardServo.setPosition(0.5);

        sleep(2000); // Run all mechanisms for 2 seconds

        // --- Step 3: Stop all mechanisms ---
        intakeMotor.setVelocity(0);
        launcherLeft.setVelocity(0);
        launcherRight.setVelocity(0);

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);
    }

    // --- Encoder drive method ---
    public void encoderDrive(double speed,
                             double leftFrontInches, double rightFrontInches,
                             double leftBackInches, double rightBackInches,
                             double timeoutS) {
        int newLeftFrontTarget;
        int newRightFrontTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        if (opModeIsActive()) {
            newLeftFrontTarget  = frontLeftDrive.getCurrentPosition() + (int)(leftFrontInches * COUNTS_PER_INCH);
            newRightFrontTarget = frontRightDrive.getCurrentPosition() + (int)(rightFrontInches * COUNTS_PER_INCH);
            newLeftBackTarget   = backLeftDrive.getCurrentPosition() + (int)(leftBackInches * COUNTS_PER_INCH);
            newRightBackTarget  = backRightDrive.getCurrentPosition() + (int)(rightBackInches * COUNTS_PER_INCH);

            frontLeftDrive.setTargetPosition(newLeftFrontTarget);
            frontRightDrive.setTargetPosition(newRightFrontTarget);
            backLeftDrive.setTargetPosition(newLeftBackTarget);
            backRightDrive.setTargetPosition(newRightBackTarget);

            frontLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            frontLeftDrive.setPower(Math.abs(speed));
            frontRightDrive.setPower(Math.abs(speed));
            backLeftDrive.setPower(Math.abs(speed));
            backRightDrive.setPower(Math.abs(speed));

            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (frontLeftDrive.isBusy() && frontRightDrive.isBusy() &&
                            backLeftDrive.isBusy() && backRightDrive.isBusy())) {

                // Optional: You can add telemetry here for debugging during the move
                // Example:
                // telemetry.addData("Running to", "%7d", newLeftFrontTarget);
                // telemetry.addData("Currently at", "%7d", frontLeftDrive.getCurrentPosition());
                // telemetry.update();
            }

            frontLeftDrive.setPower(0);
            frontRightDrive.setPower(0);
            backLeftDrive.setPower(0);
            backRightDrive.setPower(0);

            frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(100); // Small pause to ensure motors stop fully before next command
        }
    }
}
