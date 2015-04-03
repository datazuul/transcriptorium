package com.datazuul.webapps.scriptorium.domain.parameters;

public class ScaleParameters {
    private int targetWidth;
    private int targetHeight;

    public ScaleParameters(int targetWidth, int targetHeight) {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }
}
