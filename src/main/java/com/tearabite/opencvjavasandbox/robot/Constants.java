package com.tearabite.opencvjavasandbox.robot;

import com.tearabite.opencvjavasandbox.fakes.Color;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Constants {
    // CV Color Constants
    public static Scalar RED = new Scalar(0, 0, 255);
    public static Scalar GREEN = new Scalar(0, 255, 0);
    public static Scalar BLUE = new Scalar(0, 0, 255);
    public static Scalar WHITE = new Scalar(255, 255, 255);
    public static Scalar GRAY = new Scalar(80, 80, 80);
    public static Scalar BLACK = new Scalar(0, 0, 0);
    public static Scalar ORANGE = new Scalar(255, 165, 0);
    public static Scalar YELLOW = new Scalar(0, 255, 255);
    public static Scalar PURPLE = new Scalar(128, 0, 128);

    // CV Structuring Constants
    public static final Mat STRUCTURING_ELEMENT = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10));
    public static final Point ANCHOR = new Point((STRUCTURING_ELEMENT.cols() / 2f), STRUCTURING_ELEMENT.rows() / 2f);
    public static final int ERODE_DILATE_ITERATIONS = 2;
    public static final Size BLUR_SIZE = new Size(10, 10);

    // CV Camera Constants
    public static final int WEBCAM_WIDTH = 320;
    public static final int WEBCAM_HEIGHT = 240;

    // CV Invalid Detection Constants
    public static final Point INVALID_POINT = new Point(Double.MIN_VALUE, Double.MIN_VALUE);
    public static final double INVALID_AREA = -1;
    public static final Detection INVALID_DETECTION = new Detection(new Size(0, 0), 0);

    // CV Color Threshold Constants
    public static Color GREEN_UPPER = new Color(150 * 0.5, 1.0 * 255, 1.0 * 255);
    public static Color GREEN_LOWER = new Color(100 * 0.5, .1 * 255, .1 * 255);

    public static Color YELLOW_UPPER = new Color(60 * 0.5, 1.0 * 255, 1.0 * 255);
    public static Color YELLOW_LOWER = new Color(20 * 0.5, .4 * 255, .4 * 255);
}
