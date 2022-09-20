package com.tearabite.opencvjavasandbox;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;

public class CameraPane extends Pane {
    // size an image by placing it in a pane.
    CameraPane(String imageLoc) {
        this(imageLoc, "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
    }

    // size an image by placing it in a pane.
    CameraPane(String imageLoc, String style) {
        this(new SimpleStringProperty(imageLoc), new SimpleStringProperty(style));
    }

    // size a replacable image in a pane and add a replaceable style.
    CameraPane(StringProperty imageLocProperty, StringProperty styleProperty) {
        styleProperty().bind(
                new SimpleStringProperty("-fx-background-image: url(\"")
                        .concat(imageLocProperty)
                        .concat(new SimpleStringProperty("\");"))
                        .concat(styleProperty)
        );
    }
}
