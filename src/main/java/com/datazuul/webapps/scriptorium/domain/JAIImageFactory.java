package com.datazuul.webapps.scriptorium.domain;

import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedFormat;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class JAIImageFactory implements ImageFactory {

  @Override
  public Set<Formats> getSupportedInputFormats() {
    Set<Formats> formats = new HashSet<>();
    for (String supportedFormat : ImageIO.getReaderFormatNames()) {
      try {
        formats.add(JAIImage.getFormatFromString(supportedFormat));
      } catch (UnsupportedFormat ignored) {
      }
    }
    return formats;
  }

  @Override
  public Set<Formats> getSupportedOutputFormats() {
    Set<Formats> formats = new HashSet<>();
    for (String supportedFormat : ImageIO.getWriterFormatNames()) {
      try {
        formats.add(JAIImage.getFormatFromString(supportedFormat));
      } catch (UnsupportedFormat ignored) {
      }
    }
    return formats;
  }

  @Override
  public Image getImage(byte[] imageData) throws UnsupportedFormat, IOException {
    return new JAIImage(imageData);
  }
}
