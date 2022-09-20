package com.tearabite.opencvjavasandbox.robot;

import com.tearabite.opencvjavasandbox.fakes.Alliance;
import com.tearabite.opencvjavasandbox.fakes.OpenCvPipeline;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static com.tearabite.opencvjavasandbox.robot.Constants.*;
import static com.tearabite.opencvjavasandbox.robot.OpenCVUtil.getConfidenceContour;
import static com.tearabite.opencvjavasandbox.robot.OpenCVUtil.getHighGoalContour;

// Class for the pipeline that is used to detect the goals and powershots
public class TargetingPipeline extends OpenCvPipeline {
    Mat blurred = new Mat();
    Mat hsv = new Mat();
    Mat redMask1 = new Mat();
    Mat redMask2 = new Mat();
    Mat redMask = new Mat();
    Mat blueMask = new Mat();
    Mat blackMask = new Mat();
    Mat whiteMask = new Mat();
    Scalar redGoalLower1;
    Scalar redGoalUpper1;
    Scalar redGoalLower2;
    Scalar redGoalUpper2;

    private Detection red;
    private Detection blue;

    private Alliance alliance;

    public TargetingPipeline(Alliance alliance) {
        this.alliance = alliance;
    }

    // Init
    @Override
    public void init(Mat input) {
        red = new Detection(input.size(), CV_MIN_GOAL_AREA);
        blue = new Detection(input.size(), CV_MIN_GOAL_AREA);
    }

    // Process each frame that is received from the webcam
    @Override
    public Mat processFrame(Mat input)
    {
        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_RGB2HSV);
        Imgproc.line(input, new Point(0,CV_GOAL_CUTOFF_Y_LINE), new Point(input.width(),CV_GOAL_CUTOFF_Y_LINE), BLACK, 2);

        updateBlueNew(input);
        updateRedNew(input);

        return input;
    }

    private void updateRedNew(Mat input) {
        Core.inRange(hsv , new Scalar(CAMERA_WHITE_LOWER.get()), new Scalar(CAMERA_WHITE_UPPER.get()), whiteMask);
        Imgproc.erode(whiteMask, whiteMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);
        Imgproc.dilate(whiteMask, whiteMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);

        ArrayList<MatOfPoint> contoursWhite = new ArrayList<>();//80, 107
        Imgproc.findContours(whiteMask, contoursWhite, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contoursWhite.size(); i++) {
            Rect rect = Imgproc.boundingRect(contoursWhite.get(i));
            if (rect.y < CV_GOAL_CUTOFF_Y_LINE) {
                contoursWhite.remove(i);
                i--;
            } else {
                Detection newDetection = new Detection(input.size(),0);
                newDetection.setContour(contoursWhite.get(i));
                newDetection.draw(input, WHITE);
            }
        }
        MatOfPoint contour = getConfidenceContour(contoursWhite, hsv, input, Alliance.RED);
        if (contour != null) {
            red.setContour(contour);
        }

        // draw the Red Goal detection
        red.fill(input, RED);
    }

    private void updateBlueNew(Mat input) {
        Core.inRange(hsv , new Scalar(CAMERA_WHITE_LOWER.get()), new Scalar(CAMERA_WHITE_UPPER.get()), whiteMask);
        Imgproc.erode(whiteMask, whiteMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);
        Imgproc.dilate(whiteMask, whiteMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);

        ArrayList<MatOfPoint> contoursWhite = new ArrayList<>();
        Imgproc.findContours(whiteMask, contoursWhite, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contoursWhite.size(); i++) {
            Rect rect = Imgproc.boundingRect(contoursWhite.get(i));
            if (rect.y < CV_GOAL_CUTOFF_Y_LINE) {
                contoursWhite.remove(i);
                i--;
            } else {
                Detection newDetection = new Detection(input.size(),0);
                newDetection.setContour(contoursWhite.get(i));
                newDetection.draw(input, WHITE);
            }
        }
        MatOfPoint contour = getConfidenceContour(contoursWhite, hsv, input, Alliance.BLUE);
        if (contour != null) {
            blue.setContour(contour);
        }

        // draw the Red Goal detection
        blue.fill(input, BLUE);
    }

    // Update the Red Goal Detection
    private void updateRed(Mat input) {
        // take pixels that are in the color range and put them into a mask, eroding and dilating them to remove white noise
        redGoalLower1 = new Scalar(CAMERA_RED_GOAL_LOWER.getH(), CAMERA_RED_GOAL_LOWER.getS(), CAMERA_RED_GOAL_LOWER.getV());
        redGoalUpper1 = new Scalar(180, CAMERA_RED_GOAL_UPPER.getS(), CAMERA_RED_GOAL_UPPER.getV());
        redGoalLower2 = new Scalar(0, CAMERA_RED_GOAL_LOWER.getS(), CAMERA_RED_GOAL_LOWER.getV());
        redGoalUpper2 = new Scalar(CAMERA_RED_GOAL_UPPER.getH(), CAMERA_RED_GOAL_UPPER.getS(), CAMERA_RED_GOAL_UPPER.getV());
        Core.inRange(hsv, redGoalLower1, redGoalUpper1, redMask1);
        Core.inRange(hsv, redGoalLower2, redGoalUpper2, redMask2);
        Core.add(redMask1, redMask2, redMask);
        Imgproc.erode(redMask, redMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);
        Imgproc.dilate(redMask, redMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);

        // set the largest detection that was found to be the Red Goal detection
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(redMask, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {
            Detection newDetection = new Detection(input.size(),CV_MIN_GOAL_AREA,CV_MAX_GOAL_AREA);
            newDetection.setContour(contours.get(i));
            newDetection.draw(input, RED);
        }
        red.setMinArea(CV_MIN_GOAL_AREA);
        red.setMaxArea(CV_MAX_GOAL_AREA);
        red.setContour(getHighGoalContour(contours));

        // draw the Red Goal detection
        red.fill(input, RED);
    }

    // Update the Blue Goal Detection
    private void updateBlue(Mat input) {
        // take pixels that are in the color range and put them into a mask, eroding and dilating them to remove white noise
        Core.inRange(hsv, new Scalar(CAMERA_BLUE_GOAL_LOWER.get()), new Scalar(CAMERA_BLUE_GOAL_UPPER.get()), blueMask);
        Imgproc.erode(blueMask, blueMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);
        Imgproc.dilate(blueMask, blueMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);

        // set the largest detection that was found to be the Red Goal detection
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(blueMask, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {
            Detection newDetection = new Detection(input.size(),CV_MIN_GOAL_AREA, CV_MAX_GOAL_AREA);
            newDetection.setContour(contours.get(i));
            newDetection.draw(input, BLUE);
        }
        blue.setMinArea(CV_MIN_GOAL_AREA);
        blue.setMaxArea(CV_MAX_GOAL_AREA);
        blue.setContour(getHighGoalContour(contours));

        // draw the Blue Goal detection
        blue.fill(input, WHITE);
    }

    // Get the center of whatever goal is in sight
    public Point getCenterOfLargestContour() {
        if (blue.getArea() > red.getArea()) {
            return blue.getCenter();
        }

        if (red.getArea() > blue.getArea()) {
            return red.getCenter();
        }

        return INVALID_POINT;
    }

    // Get the area of whatever goal is in sight
    public double getAreaOfLargestContour() {
        return Math.max(blue.getArea(), red.getArea());
    }

    // Get the Red Goal Detection
    public Detection getRed() {
        return red;
    }

    // Get the Blue Goal Detection
    public Detection getBlue() {
        return blue;
    }
}
