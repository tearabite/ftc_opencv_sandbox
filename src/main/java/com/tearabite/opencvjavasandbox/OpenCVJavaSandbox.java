package com.tearabite.opencvjavasandbox;

import javafx.scene.layout.AnchorPane;
import org.opencv.core.Core;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenCVJavaSandbox extends Application {

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            AnchorPane rootElement = loader.load();
            Scene scene = new Scene(rootElement, 1100, 450);
            primaryStage.setTitle("JavaFX meets OpenCV");
            primaryStage.setScene(scene);
            rootElement.prefHeightProperty().bind(primaryStage.getScene().heightProperty());
            rootElement.prefWidthProperty().bind(primaryStage.getScene().widthProperty());
            primaryStage.show();
            UIController controller = loader.getController();
            primaryStage.setOnCloseRequest((we -> controller.setClosed()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}