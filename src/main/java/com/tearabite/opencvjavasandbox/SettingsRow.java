package com.tearabite.opencvjavasandbox;

import lombok.*;
import org.opencv.core.Size;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;

public class SettingsRow {
    public static SettingsRow fromField(Field field) {
        return new SettingsRow(field);
    }

    private SettingsRow(Field field) {
        this.label = field.getAnnotation(Setting.class).label();
        this.field = field;
    }

    Field field;
    @Getter String label;

    public String getValueAsString() throws IllegalAccessException {
        if (this.field.getType().equals(String.class)) {
            return (String)this.field.get(Settings.getInstance());
        } else if (this.field.getType().equals(Integer.class)) {
            return Integer.toString((Integer)this.field.get(Settings.getInstance()));
        } else if (this.field.getType().equals(Size.class)) {
            return this.field.get(Settings.getInstance()).toString();
        }

        return "N/A";
    }

    public void setValueFromString(String value) throws IllegalAccessException {
        if (field.getType().equals(String.class)) {
            field.set(Settings.getInstance(), value);
        } else if (field.getType().equals(Integer.class)) {
            field.set(Settings.getInstance(), Integer.parseInt(value));
        } else if (field.getType().equals(Size.class)) {
            String sizeString = value.toLowerCase();
            String[] parts = sizeString.split("x");
            if (parts.length != 2) {
                throw new InvalidParameterException("Could not parse value to Size");
            }
            int width = Integer.parseInt(parts[0]);
            int height = Integer.parseInt(parts[1]);
            field.set(Settings.getInstance(), new Size(width, height));
        }
    }
}
