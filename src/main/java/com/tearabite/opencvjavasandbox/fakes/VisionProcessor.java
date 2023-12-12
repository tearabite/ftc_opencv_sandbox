package com.tearabite.opencvjavasandbox.fakes;

import javafx.scene.canvas.Canvas;
import org.opencv.core.Mat;

public interface VisionProcessor {
    void init(int width, int height, CameraCalibration calibration);
    Object processFrame(Mat frame, long captureTimeNanos);
    void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext);

}
