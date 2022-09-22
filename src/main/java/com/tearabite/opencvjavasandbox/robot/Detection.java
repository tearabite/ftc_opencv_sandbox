package com.tearabite.opencvjavasandbox.robot;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tearabite.opencvjavasandbox.robot.Constants.*;
import static com.tearabite.opencvjavasandbox.robot.OpenCVUtil.*;

// Class for a Detection
public class Detection {
    private double minAreaPx;
    private double maxAreaPx;
    private final Size maxSizePx;
    private double areaPx =  INVALID_AREA;
    private Point centerPx = INVALID_POINT;
    private Point bottomLeftPx = INVALID_POINT;
    private Point bottomRightPx = INVALID_POINT;
    private MatOfPoint contour;

    // Constructor
    public Detection(Size frameSize, double minAreaFactor) {
        this.maxSizePx = frameSize;
        this.minAreaPx = frameSize.area() * minAreaFactor;
        this.maxAreaPx = frameSize.area();
    }

    public Detection(Size frameSize, double minAreaFactor, double maxSizeFactor) {
        this.maxSizePx = frameSize;
        this.minAreaPx = frameSize.area() * minAreaFactor;
        this.maxAreaPx = frameSize.area() * maxSizeFactor;
    }

    public void setMinArea(double minAreaFactor) {
        this.minAreaPx = maxSizePx.area() * minAreaFactor;
    }

    public void setMaxArea(double maxAreaFactor) {
        this.minAreaPx = maxSizePx.area() * maxAreaFactor;
    }

    // Draw a convex hull around the current detection on the given image
    public void draw(Mat img, Scalar color, boolean fill) {
        if (isValid()) {
            if (fill) {
                fillConvexHull(img, contour, color);
            } else {
                drawConvexHull(img, contour, color);
            }
            drawPoint(img, centerPx, GREEN, 3);
        }
    }

    public void drawAngledRect(Mat img, Scalar color, boolean fill) {
        if (isValid()) {
            OpenCVUtil.drawAngledRect(img, contour, color, fill);
        }
    }

    // Check if the current Detection is valid
    public boolean isValid() {
//        return true;
        return (this.contour != null) && (this.centerPx != INVALID_POINT) && (this.areaPx != INVALID_AREA);
    }

    // Get the current contour
    public MatOfPoint getContour() {
        return contour;
    }

    // Set the values of the current contour
    public void setContour(MatOfPoint contour) {
        this.contour = contour;

        double area;
        if (contour != null && (area = Imgproc.contourArea(contour)) > minAreaPx && area < maxAreaPx) {
            this.areaPx = area;
            this.centerPx = getCenterOfContour(contour);
            this.bottomLeftPx = getBottomLeftOfContour(contour);
            this.bottomRightPx = getBottomRightOfContour(contour);
        } else {
            this.areaPx = INVALID_AREA;
            this.centerPx = INVALID_POINT;
            this.bottomLeftPx = INVALID_POINT;
            this.bottomRightPx = INVALID_POINT;
        }
    }

    // Returns the center of the Detection, normalized so that the width and height of the frame is from [-50,50]
    public Point getCenter() {
        if (!isValid()) {
            return INVALID_POINT;
        }

        double normalizedX = ((centerPx.x / maxSizePx.width) * 100) - 50;
        double normalizedY = ((centerPx.y / maxSizePx.height) * -100) + 50;

        return new Point(normalizedX, normalizedY);
    }

    // Get the center point in pixels
    public Point getCenterPx() {
        return centerPx;
    }

    // Get the area of the Detection, normalized so that the area of the frame is 100
    public double getArea() {
        if (!isValid()) {
            return INVALID_AREA;
        }

        return (areaPx / (maxSizePx.width * maxSizePx.height)) * 100;
    }

    // Get the leftmost bottom corner of the detection
    public Point getBottomLeftCornerPx() {
        return bottomLeftPx;
    }

    // Get the rightmost bottom corner of the detection
    public Point getBottomRightCornerPx() {
        return bottomRightPx;
    }

    public double getWidthOfAngledRect() {
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
        Point[] vertices = new Point[4];
        rect.points(vertices);
        Point highest = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        Point secondHighest = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].y < highest.y) {
                if (highest.y < secondHighest.y) {
                    secondHighest = highest;
                }
                highest = vertices[i];
            } else if (vertices[i].y < secondHighest.y) {
                secondHighest = vertices[i];
            }
        }

        double x1 = secondHighest.x;
        double x2 = highest.x;
        double y1 = secondHighest.y;
        double y2 = highest.y;

        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

        return distance;
    }

    public Point getTopCenterOfAngledRect() {
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
        Point[] vertices = new Point[4];
        rect.points(vertices);
        Point highest = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        Point secondHighest = new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i].y < highest.y) {
                if (highest.y < secondHighest.y) {
                    secondHighest = highest;
                }
                highest = vertices[i];
            } else if (vertices[i].y < secondHighest.y) {
                secondHighest = vertices[i];
            }
        }

        double x1 = secondHighest.x;
        double x2 = highest.x;
        double y1 = secondHighest.y;
        double y2 = highest.y;
        Point m = new Point((x1 + x2) / 2, (y1 + y2) / 2);
        return m;
    }
}