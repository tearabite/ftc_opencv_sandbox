package com.tearabite.opencvjavasandbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

public class OpenCVJavaSandbox extends Application {

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            AnchorPane rootElement = loader.load();
            Scene scene = new Scene(rootElement, 640, 480);
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
        OpenCV.loadLocally();
        launch(args);
    }
}