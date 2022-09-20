package com.tearabite.opencvjavasandbox;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.tearabite.opencvjavasandbox.fakes.Alliance;
import com.tearabite.opencvjavasandbox.fakes.OpenCvPipeline;
import com.tearabite.opencvjavasandbox.robot.TargetingPipeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UIController {
    @FXML
    private Button startButton;
    @FXML
    private BorderPane imageRoot;
    @FXML
    private ImageView currentFrame;

    @FXML
    private TextField maxFps;
    @FXML
    private ComboBox<Size> imageSize;

    private ScheduledExecutorService timer;
    private ScheduledFuture<?> timerFuture;
    private final VideoCapture capture = new VideoCapture();
    private boolean cameraActive = false;
    private Runnable frameGrabber;
    private int framerate = 30;

    // Change these as needed
    private static int cameraId = 0;
    private final OpenCvPipeline pipeline = new TargetingPipeline(Alliance.RED);

    public void initialize() {
        // Bind the width of the current frame to the size of its container.
        currentFrame.fitWidthProperty().bind(imageRoot.widthProperty());
        imageRoot.setCenter(currentFrame);

        setFps(framerate);



    }

    /**
     * The action triggered by pushing the button on the GUI
     *
     * @param event the push button event
     */
    @FXML
    protected void startCamera(ActionEvent event) {

        if (!this.cameraActive) {
            this.capture.open(cameraId);

            if (this.capture.isOpened()) {
                this.cameraActive = true;

                frameGrabber = new Runnable() {
                    private boolean isFirstFrame = true;

                    @Override
                    public void run() {
                        Mat frame = grabFrame();
                        if (isFirstFrame) {
                            initImageSizeOptions(frame);
                            pipeline.init(frame);
                            isFirstFrame = false;
                        }

                        Image imageToShow;
                        if (imageSize.getValue() != null) {
                            Mat resized = new Mat();
                            Imgproc.resize(frame, resized, imageSize.getValue(), Imgproc.INTER_AREA);
                            imageToShow = Utils.mat2Image(resized);
                        } else {
                            imageToShow = Utils.mat2Image(frame);
                        }
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timerFuture = this.timer.scheduleAtFixedRate(frameGrabber, 0, 1000 / this.framerate, TimeUnit.MILLISECONDS);

                this.startButton.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            this.cameraActive = false;
            this.startButton.setText("Start Camera");
            this.stopAcquisition();
        }
    }

    private void initImageSizeOptions(Mat frame) {
        int width = frame.width();
        int height = frame.height();
        ObservableList<Size> options =
                FXCollections.observableArrayList(
                        new Size(width, height),
                        new Size(width / 2, height / 2),
                        new Size(width / 4, height / 4),
                        new Size(width / 8, height / 8)
                );
        Utils.onFXThread(imageSize.itemsProperty(), options);
        Utils.onFXThread(imageSize.valueProperty(), options.get(0));
    }

    private Mat grabFrame() {
        Mat frame = new Mat();

        if (this.capture.isOpened()) {
            try {
                this.capture.read(frame);

                if (!frame.empty()) {
                    frame = pipeline.processFrame(frame);
                }
            } catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(1000 / this.framerate, TimeUnit.MILLISECONDS);
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

    public void setFps(int fps) {
        if (fps <= 0) {
            throw new NumberFormatException("Framerate cannot be zero or negative");
        }

        if (timerFuture != null) {
            timerFuture.cancel(false);
        }
        if (this.cameraActive) {
            this.timerFuture = this.timer.scheduleAtFixedRate(frameGrabber, 0, 1000 / fps, TimeUnit.MILLISECONDS);
        }
        this.maxFps.setText(Integer.toString(fps));
    }

    public void maxFpsKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            try {
                setFps(Integer.parseInt(maxFps.getText()));
            } catch (NumberFormatException e) {
                maxFps.setText(Integer.toString(framerate));
            }
        }
    }
}
