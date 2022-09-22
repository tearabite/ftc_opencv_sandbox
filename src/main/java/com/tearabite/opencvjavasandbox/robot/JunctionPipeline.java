package com.tearabite.opencvjavasandbox.robot;

import com.tearabite.opencvjavasandbox.fakes.OpenCvPipeline;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.tearabite.opencvjavasandbox.robot.Constants.*;
import static com.tearabite.opencvjavasandbox.robot.Constants.ERODE_DILATE_ITERATIONS;

public class JunctionPipeline extends OpenCvPipeline {
    Mat blurred = new Mat();
    Mat hsv = new Mat();
    Mat colorMask = new Mat();

    List<Detection> detections = new ArrayList<>();

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.blur(input, blurred, BLUR_SIZE);
        Imgproc.cvtColor(blurred, hsv, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsv , new Scalar(YELLOW_LOWER.get()), new Scalar(YELLOW_UPPER.get()), colorMask);
        Imgproc.erode(colorMask, colorMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);
        Imgproc.dilate(colorMask, colorMask, STRUCTURING_ELEMENT, ANCHOR, ERODE_DILATE_ITERATIONS);

        detections.clear();
        ArrayList<MatOfPoint> colorContours = new ArrayList<>();
        Imgproc.findContours(colorMask, colorContours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Detection closestDetection = null;
        double closestWidth = 0;
        for (int i = 0; i < colorContours.size(); i++) {
            Detection detection = new Detection(input.size(),0.005);
            detection.setContour(colorContours.get(i));
            detections.add(detection);

            if (detection.isValid()) {
                Point p = detection.getTopCenterOfAngledRect();
                OpenCVUtil.drawPoint(input, p, RED, 10);

                double width = detection.getWidthOfAngledRect();
                if (width > closestWidth) {
                    closestDetection = detection;
                    closestWidth = width;
                }
            }
        }

        if (closestDetection != null) {
            OpenCVUtil.drawPoint(input, closestDetection.getTopCenterOfAngledRect(), GREEN, 10);
        }

        return input;
    }
}
