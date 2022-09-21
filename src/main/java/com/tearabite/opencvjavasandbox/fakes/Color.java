package com.tearabite.opencvjavasandbox.fakes;

public class Color {
    public Color(double h, double s, double v) {
        this.h = h;
        this.s = s;
        this.v = v;
    }

    public double[] get() {
        return new double[]{h, s, v};
    }

    public double getH() {
        return this.h;
    }

    public double getS() {
        return this.s;
    }

    public double getV() {
        return this.v;
    }

    private final double h;
    private final double s;
    private final double v;

    public static Color fromUIColor(javafx.scene.paint.Color color) {
        float[] hsv = java.awt.Color.RGBtoHSB(
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                null);
        hsv[0] *= 180;
        hsv[1] *= 255;
        hsv[2] *= 255;

        return new Color(hsv[0], hsv[1], hsv[2]);
    }

    @Override
    public String toString() {
        return String.format("%f, %f, %f", this.h, this.s, this.v);
    }
}
