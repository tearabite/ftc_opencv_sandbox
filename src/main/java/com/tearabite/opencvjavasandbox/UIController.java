package com.tearabite.opencvjavasandbox;

import com.tearabite.opencvjavasandbox.fakes.VisionProcessor;
import com.tearabite.opencvjavasandbox.robot.JunctionPipeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

public class UIController {
    @FXML
    public AnchorPane root;
    @FXML
    private ImageView currentFrame;

    private ScheduledExecutorService timer;
    private final VideoCapture capture = new VideoCapture();
    private boolean cameraActive = false;

    // Change these as needed
    private static int cameraId = 0;
    private final VisionProcessor pipeline = new JunctionPipeline();

    public void initialize() {
        currentFrame.fitWidthProperty().bind(root.widthProperty());
        currentFrame.fitHeightProperty().bind(root.heightProperty());

        startCamera();
    }

    private void startCamera() {
        if (!this.cameraActive) {
            this.capture.open(cameraId);

            if (this.capture.isOpened()) {
                this.capture.set(CAP_PROP_FRAME_WIDTH, 640);
                this.capture.set(CAP_PROP_FRAME_HEIGHT, 480);
                this.cameraActive = true;

                Runnable frameGrabber = new Runnable() {
                    private boolean isFirstFrame = true;

                    @Override
                    public void run() {
                        Mat frame = grabFrame();
                        if (isFirstFrame) {
                            pipeline.init(frame.cols(), frame.rows(), null);
                            isFirstFrame = false;
                        }

                        Image imageToShow = Utils.mat2Image(frame);
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 1000 / 15, TimeUnit.MILLISECONDS);
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            this.cameraActive = false;
            this.stopAcquisition();
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();

        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);

                if (!frame.empty()) {
                    pipeline.processFrame(frame, 0);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(1000 / 15, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view  the {@link ImageView} to update
     * @param image the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    protected void setClosed() {
        this.stopAcquisition();
    }
}
