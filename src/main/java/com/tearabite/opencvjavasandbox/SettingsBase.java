package com.tearabite.opencvjavasandbox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Field;
import java.util.Arrays;

abstract public class SettingsBase {
    private static Settings singleton;
    private static ObservableList<SettingsRow> observableList;

    public static synchronized Settings getInstance( ) {
        if (singleton == null) {
            singleton = new Settings();
        }
        return singleton;
    }

    public static ObservableList<SettingsRow> getObservableList() throws IllegalAccessException {
        if (observableList == null) {
            observableList = FXCollections.observableArrayList();
            Class<Settings> clazz = Settings.class;
            for (Field field : Arrays.stream(clazz.getFields()).filter(f -> f.isAnnotationPresent(Setting.class)).toList()) {
                observableList.add(SettingsRow.fromField(field));
            }
        }

        return observableList;
    }
}
