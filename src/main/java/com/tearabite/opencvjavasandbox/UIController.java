package com.tearabite.opencvjavasandbox;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.tearabite.opencvjavasandbox.fakes.Color;
import com.tearabite.opencvjavasandbox.fakes.OpenCvPipeline;
import com.tearabite.opencvjavasandbox.robot.JunctionPipeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.tearabite.opencvjavasandbox.robot.Constants.YELLOW_LOWER;
import static com.tearabite.opencvjavasandbox.robot.Constants.YELLOW_UPPER;
import static com.tearabite.opencvjavasandbox.robot.OpenCVUtil.toRGBCode;

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
    @FXML
    private ColorPicker yellowLower;
    @FXML
    private ColorPicker yellowUpper;

    private static final int MAX_PIXEL_WALK_HUE_DEVIATION = 10;
    private ScheduledExecutorService timer;
    private ScheduledFuture<?> timerFuture;
    private final VideoCapture capture = new VideoCapture();
    private boolean cameraActive = false;
    private Runnable frameGrabber;
    private int framerate = 30;

    // Change these as needed
    private static int cameraId = 0;
    private final OpenCvPipeline pipeline = new JunctionPipeline();

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

    public void viewportClicked(MouseEvent mouseEvent) {
        double uiX = mouseEvent.getX();
        double uiY = mouseEvent.getY();
        double uiWidth = currentFrame.getBoundsInLocal().getWidth();
        double uiHeight = currentFrame.getBoundsInLocal().getHeight();
        double ratioX = uiX / uiWidth;
        double ratioY = uiY / uiHeight;
        double imageWidth = currentFrame.getImage().getWidth();
        double imageHeight = currentFrame.getImage().getHeight();
        int x = (int) (imageWidth * ratioX);
        int y = (int) (imageHeight * ratioY);

        PixelReader pr = currentFrame.getImage().getPixelReader();
        javafx.scene.paint.Color selectedColor = pr.getColor(x, y);
        double lowerH = selectedColor.getHue();
        double upperH = selectedColor.getHue();
        double previH = selectedColor.getHue();

        // Walk Left
        for (int i = x; i > 0; i--) {
            javafx.scene.paint.Color c = pr.getColor(i, y);
            if (Math.abs(c.getHue() - previH) > MAX_PIXEL_WALK_HUE_DEVIATION) {
                break;
            }

            if (c.getHue() < lowerH) {
                lowerH = c.getHue();
            }

            if (c.getHue() > upperH) {
                upperH = c.getHue();
            }

            previH = c.getHue();
        }

        // Walk Right
        for (int i = x; i < imageWidth; i++) {
            javafx.scene.paint.Color c = pr.getColor(i, y);
            if (Math.abs(c.getHue() - previH) > MAX_PIXEL_WALK_HUE_DEVIATION) {
                break;
            }

            if (c.getHue() < lowerH) {
                lowerH = c.getHue();
            }

            if (c.getHue() > upperH) {
                upperH = c.getHue();
            }

            previH = c.getHue();
        }

        Color lower = new Color(lowerH, 0.3 * 255, 0.3 * 255);
        Color upper = new Color(upperH, 1.0 * 255, 1.0 * 255);

        imageRoot.setStyle(String.format("-fx-background-color: %s", toRGBCode(selectedColor)));

        // TODO: These are sort of pipeline specific. We should abstract this more.
        YELLOW_UPPER = upper;
        YELLOW_LOWER = lower;
    }
}
