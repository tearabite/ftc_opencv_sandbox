package com.tearabite.opencvjavasandbox.robot;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.tearabite.opencvjavasandbox.robot.Constants.*;
import static com.tearabite.opencvjavasandbox.robot.OpenCVUtil.*;

// Class for a Detection
public class Detection {
    private double minAreaPx;
    private double maxAreaPx;
    private final Size maxSizePx;
    private double areaPx = INVALID_AREA;
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

    public boolean isValid() {
//        return true;
        return (this.contour != null) && (this.centerPx != INVALID_POINT) && (this.areaPx != INVALID_AREA);
    }

    public MatOfPoint getContour() {
        return contour;
    }

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

    public Point getCenter() {
        if (!isValid()) {
            return INVALID_POINT;
        }

        double normalizedX = ((centerPx.x / maxSizePx.width) * 100) - 50;
        double normalizedY = ((centerPx.y / maxSizePx.height) * -100) + 50;

        return new Point(normalizedX, normalizedY);
    }

    public Point getCenterPx() {
        return centerPx;
    }

    public double getArea() {
        if (!isValid()) {
            return INVALID_AREA;
        }

        return (areaPx / (maxSizePx.width * maxSizePx.height)) * 100;
    }

    public Point getBottomLeftCornerPx() {
        return bottomLeftPx;
    }

    public Point getBottomRightCornerPx() {
        return bottomRightPx;
    }

    private List<Point> getSsortedAngledRectVertices() {
        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
        Point[] vertices = new Point[4];
        rect.points(vertices);
        return Arrays.stream(vertices).sorted(Comparator.comparingDouble(o -> o.y)).toList();
    }

    public double getWidthOfAngledRect() {
        List<Point> angledRectVerticies = getSsortedAngledRectVertices();

        double x1 = angledRectVerticies.get(1).x;
        double x2 = angledRectVerticies.get(0).x;
        double y1 = angledRectVerticies.get(1).y;
        double y2 = angledRectVerticies.get(0).y;

        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public Point getTopCenterOfAngledRect() {
        List<Point> angledRectVerticies = getSsortedAngledRectVertices();

        double x1 = angledRectVerticies.get(1).x;
        double x2 = angledRectVerticies.get(0).x;
        double y1 = angledRectVerticies.get(1).y;
        double y2 = angledRectVerticies.get(0).y;

        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }
}