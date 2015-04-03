package com.datazuul.webapps.scriptorium.domain.parameters;

public class CropParameters {
    private final int horizontalOffset;
    private final int verticalOffset;
    private final int width;
    private final int height;

    public CropParameters(int horizontalOffset, int verticalOffset, int width, int height) {
        this.horizontalOffset = horizontalOffset;
        this.verticalOffset = verticalOffset;
        this.width = width;
        this.height = height;
    }

    public int getHorizontalOffset() {
        return horizontalOffset;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
