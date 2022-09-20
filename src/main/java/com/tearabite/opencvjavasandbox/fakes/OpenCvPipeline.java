package com.tearabite.opencvjavasandbox.fakes;

import org.opencv.core.Mat;

public abstract class OpenCvPipeline {
    public abstract Mat processFrame(Mat input);

    public void onViewportTapped() {
    }

    public void init(Mat mat) {
    }
}
