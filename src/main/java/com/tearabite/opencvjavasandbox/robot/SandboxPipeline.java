package com.tearabite.opencvjavasandbox.robot;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.*;

public class SandboxPipeline {

    static Mat hsv = new Mat();
    static Mat whiteMask = new Mat();
    // CV Structuring Constants
    public static final Mat STRUCTURING_ELEMENT = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
    public static final Point ANCHOR = new Point((STRUCTURING_ELEMENT.cols() / 2f), STRUCTURING_ELEMENT.rows() / 2f);
    public static final int ERODE_DILATE_ITERATIONS = 2;
    public static final Size BLUR_SIZE = new Size(7, 7);

    public static Mat processFrame(Mat incomingFrame) {
        Mat resultFrame = incomingFrame.clone();

        // Do fancy stuff here

        Imgproc.cvtColor(incomingFrame, hsv, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsv , new Scalar(0, 0, 40), new Scalar(180, 30, 255), whiteMask);
        Imgproc.erode(whiteMask, whiteMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);
        Imgproc.dilate(whiteMask, whiteMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);

        return whiteMask;
    }
}
