package com.tearabite.opencvjavasandbox.fakes;

public class Color {
    public Color(double h, double s, double v) {
        this.h = h;
        this.s = s;
        this.v = v;
    }

    public double[] get() {
        return new double[] { h, s, v};
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

    public double h;
    public double s;
    public double v;

    @Override
    public String toString() {
        return String.format("%f, %f, %f", this.h, this.s, this.v);
    }
}
