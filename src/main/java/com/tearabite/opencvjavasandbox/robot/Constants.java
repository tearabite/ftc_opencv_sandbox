package com.tearabite.opencvjavasandbox.robot;

import com.tearabite.opencvjavasandbox.fakes.Color;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Constants {
    // CV Color Constants
    public static Scalar RED = new Scalar(255, 0, 0);
    public static Scalar GREEN = new Scalar(0, 255, 0);
    public static Scalar BLUE = new Scalar(0, 0, 255);
    public static Scalar WHITE = new Scalar(255, 255, 255);
    public static Scalar GRAY = new Scalar(80, 80, 80);
    public static Scalar BLACK = new Scalar(0, 0, 0);
    public static Scalar ORANGE = new Scalar(255, 165, 0);
    public static Scalar YELLOW = new Scalar(255, 255, 0);
    public static Scalar PURPLE = new Scalar(128, 0, 128);

    // CV Structuring Constants
    public static final Mat STRUCTURING_ELEMENT = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
    public static final Point ANCHOR = new Point((STRUCTURING_ELEMENT.cols() / 2f), STRUCTURING_ELEMENT.rows() / 2f);
    public static final int ERODE_DILATE_ITERATIONS = 2;
    public static final Size BLUR_SIZE = new Size(7, 7);

    // CV Camera Constants
    public static final int WEBCAM_WIDTH = 320;
    public static final int WEBCAM_HEIGHT = 240;

    // CV Invalid Detection Constants
    public static final Point INVALID_POINT = new Point(Double.MIN_VALUE, Double.MIN_VALUE);
    public static final double INVALID_AREA = -1;
    public static final Detection INVALID_DETECTION = new Detection(new Size(0, 0), 0);

    public static double R_ARM_POWER = 0.2;
    public static double R_ARM_SPEED = 20;
    public static int R_ARM_DEFAULT_POS = 0;
    public static int R_ARM_UP_POS = 221;
    public static int R_ARM_ALMOST_DOWN_POS = 650;
    public static int R_ARM_DOWN_POS = 750;
    public static double R_CLAW_CLOSED = 0.13;
    public static double R_CLAW_OPEN = 0.7;
    public static double R_INTAKE_SPEED = 0.9;
    public static double R_INTAKE_SHIELD_UP = 0.17;//0.05
    public static double R_INTAKE_SHIELD_DOWN = 0.68;//0.95
    public static double R_INTAKE_SHIELD_SPEED = 0.04;
    public static double R_SHOOTER_GOAL_POWER = 0.66;
    public static double R_SHOOTER_MID_GOAL_POWER = 0.54;
    public static double R_SHOOTER_POWERSHOT_POWER = 0.57;
    public static double R_PUSHER_CLOSED = 0.35;
    public static double R_PUSHER_OPEN = 0.55;
    public static double R_PUSHER_DELAY = 0.15;

    // CV Color Threshold Constants
    public static Color CAMERA_RED_GOAL_LOWER       = new Color(165, 80, 80);
    public static Color CAMERA_RED_GOAL_UPPER       = new Color(15, 255, 255);
    public static Color CAMERA_RED_POWERSHOT_LOWER  = new Color(165, 80, 80);
    public static Color CAMERA_RED_POWERSHOT_UPPER  = new Color(15, 255, 255);
    public static Color CAMERA_BLUE_GOAL_LOWER      = new Color(75, 40, 80);
    public static Color CAMERA_BLUE_GOAL_UPPER      = new Color(120, 255, 255);
    public static Color CAMERA_BLUE_POWERSHOT_LOWER = new Color(75, 30, 50);
    public static Color CAMERA_BLUE_POWERSHOT_UPPER = new Color(120, 255, 255);
    public static Color CAMERA_ORANGE_LOWER         = new Color(0, 70, 50);
    public static Color CAMERA_ORANGE_UPPER         = new Color(60, 255, 255);
    public static Color CAMERA_WHITE_LOWER          = new Color(0, 0, 40);
    public static Color CAMERA_WHITE_UPPER          = new Color(180, 30, 255);

    // CV Detection Constants
    public static double CV_MIN_STARTERSTACK_AREA = 0;
    public static double CV_MIN_STARTERSTACK_SINGLE_AREA = 0.08;
    public static double CV_MIN_STARTERSTACK_QUAD_AREA = 1.3;
    public static double CV_MIN_GOAL_AREA = 0;
    public static double CV_MAX_GOAL_AREA = 0.3;
    public static double CV_MIN_POWERSHOT_AREA = 0.001;
    public static double CV_MAX_POWERSHOT_AREA = 0.05;
    public static Point CV_POWERSHOT_OFFSET = new Point(-3, -20); // offset from the bottom left of the goal to the top right of the powershot box (for red)
    public static Size CV_POWERSHOT_DIMENSIONS = new Size(100, 50);

    public static Size CV_GOAL_PROPER_ASPECT = new Size(11, 8.5);
    public static double CV_GOAL_PROPER_AREA = 1.25;
    public static double CV_GOAL_ALLOWABLE_AREA_ERROR = 1;
    public static double CV_GOAL_ALLOWABLE_SOLIDARITY_ERROR = 1;
    public static double CV_GOAL_CUTOFF_Y_LINE = 65;
    public static double CV_GOAL_PROPER_HEIGHT = 107;
    public static double CV_GOAL_MIN_CONFIDENCE = 3;

    public static Color CV_POWERSHOT_OFFSETS_RED = new Color(-40, -30, -19);
    public static Color CV_POWERSHOT_OFFSETS_BLUE = new Color(40, 30, 19);

    // Old CV Detection Constants
    public static double CV_GOAL_SIDE_ALLOWABLE_Y_ERROR = 20;
    public static double CV_GOAL_SIDE_ALLOWABLE_SIZE_ERROR = 100;
    public static Size CV_GOAL_SIDE_ASPECT_RATIO = new Size(6.5,15.5);
    public static double CV_GOAL_SIDE_ALLOWABLE_ASPECT_ERROR = 10;

    // Hardware Name Constants
    public static final String WHEEL_FRONT_LEFT = "frontLeft";
    public static final String WHEEL_FRONT_RIGHT = "frontRight";
    public static final String WHEEL_BACK_LEFT = "backLeft";
    public static final String WHEEL_BACK_RIGHT = "backRight";
    public static final String ARM = "wobbler";
    public static final String CLAW = "claw";
    public static final String INTAKE = "intake";
    public static final String INTAKE_SECONDARY = "secondary";
    public static final String INTAKE_SHIELD = "shield";
    public static final String SHOOTER = "wheel";
    public static final String PUSHER = "pusher";
    public static final String STACK_WEBCAM = "Stack Webcam";
    public static final String TARGETING_WEBCAM = "Targeting Webcam";
    public static final String IMU_SENSOR = "imu";
}
