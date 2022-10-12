module com.tearabite.opencvjavasandbox {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires opencv;
    requires java.desktop;
    requires static lombok;

    opens com.tearabite.opencvjavasandbox to javafx.fxml;
    exports com.tearabite.opencvjavasandbox;
    exports com.tearabite.opencvjavasandbox.robot;
    opens com.tearabite.opencvjavasandbox.robot to javafx.fxml;
    exports com.tearabite.opencvjavasandbox.fakes;
    opens com.tearabite.opencvjavasandbox.fakes to javafx.fxml;

}