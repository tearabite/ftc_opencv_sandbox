package com.tearabite.opencvjavasandbox;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.tearabite.opencvjavasandbox.fakes.Color;
import com.tearabite.opencvjavasandbox.fakes.VisionProcessor;
import com.tearabite.opencvjavasandbox.robot.JunctionPipeline;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.PixelReader;
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
    private TableView<SettingsRow> settings;

    private static final int MAX_PIXEL_WALK_HUE_DEVIATION = 10;
    private ScheduledExecutorService timer;
    private ScheduledFuture<?> timerFuture;
    private final VideoCapture capture = new VideoCapture();
    private boolean cameraActive = false;
    private Runnable frameGrabber;

    // Change these as needed
    private static int cameraId = 0;
    private final VisionProcessor pipeline = new JunctionPipeline();

    public void initialize() throws IllegalAccessException {
        // Bind the width of the current frame to the size of its container.

        currentFrame.fitWidthProperty().bind(imageRoot.widthProperty());
        imageRoot.setCenter(currentFrame);


        TableColumn<SettingsRow, String> keyColumn = (TableColumn<SettingsRow, String>) settings.getColumns().get(0);
        TableColumn<SettingsRow, String> valueColumn = (TableColumn<SettingsRow, String>) settings.getColumns().get(1);

        keyColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("valueAsString"));
        valueColumn.setOnEditCommit(e -> {
            try {
                e.getRowValue().setValueFromString(e.getNewValue());
            } catch (Exception ex) {
                // Swallow the exception
            } finally {
                e.getTableView().refresh();
            }
        });
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        settings.setItems(Settings.getObservableList());
        settings.setEditable(true);
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
                            pipeline.init(frame.cols(), frame.rows(), null);
                            isFirstFrame = false;

                            Settings.imageSize = new Size(frame.width(), frame.height());
                            settings.refresh();
                        }

                        Image imageToShow;
                        Mat resized = new Mat();
                        Imgproc.resize(frame, resized, Settings.imageSize, Imgproc.INTER_AREA);
                        imageToShow = Utils.mat2Image(resized);
                        updateImageView(currentFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timerFuture = this.timer.scheduleAtFixedRate(frameGrabber, 0, 1000 / Settings.FPS, TimeUnit.MILLISECONDS);

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

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(1000 / Settings.FPS, TimeUnit.MILLISECONDS);
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
