package com.datazuul.webapps.scriptorium.domain;

import com.datazuul.webapps.scriptorium.domain.exceptions.InvalidParametersException;
import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedOperation;
import com.datazuul.webapps.scriptorium.domain.parameters.CropParameters;
import com.datazuul.webapps.scriptorium.domain.parameters.ScaleParameters;
import java.io.IOException;

public interface Image {

    abstract Formats getFormat();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract byte[] toByteArray() throws IOException;

    public abstract Image crop(CropParameters params) throws UnsupportedOperation, InvalidParametersException;

    public abstract Image scale(ScaleParameters params) throws UnsupportedOperation, InvalidParametersException;

    public abstract Image rotate(int arcDegree) throws UnsupportedOperation, InvalidParametersException;

    public abstract Image toDepth(BitDepths depth) throws UnsupportedOperation;

    public abstract Image convert(Formats format) throws UnsupportedOperation;

}
