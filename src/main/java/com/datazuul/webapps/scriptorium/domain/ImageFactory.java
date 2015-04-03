package com.datazuul.webapps.scriptorium.domain;

import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedFormat;
import java.io.IOException;
import java.util.Set;

public interface ImageFactory {

    public Set<Formats> getSupportedInputFormats();

    public Set<Formats> getSupportedOutputFormats();

    public Image getImage(byte[] imageData) throws UnsupportedFormat, IOException;
}
