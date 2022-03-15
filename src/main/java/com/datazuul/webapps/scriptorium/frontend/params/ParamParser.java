package com.datazuul.webapps.scriptorium.frontend.params;

import com.datazuul.webapps.scriptorium.domain.BitDepths;
import com.datazuul.webapps.scriptorium.domain.Formats;
import com.datazuul.webapps.scriptorium.domain.exceptions.InvalidParametersException;
import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedFormat;
import com.datazuul.webapps.scriptorium.domain.parameters.CropParameters;
import com.datazuul.webapps.scriptorium.domain.parameters.ScaleParameters;

public class ParamParser {

  public static CropParameters getCropParameters(String cropRegion, int origWidth, int origHeight)
      throws InvalidParametersException {
    String[] groups = cropRegion.split(",");
    if (groups.length != 4) {
      throw new InvalidParametersException();
    }
    try {
      return new CropParameters(
          Integer.parseInt(groups[0]),
          Integer.parseInt(groups[1]),
          Integer.parseInt(groups[2]),
          Integer.parseInt(groups[3]));
    } catch (NumberFormatException e) {
      float left = Float.parseFloat(groups[0]) * (float) origWidth;
      float top = Float.parseFloat(groups[1]) * (float) origHeight;
      float width = Float.parseFloat(groups[2]) * (float) origWidth;
      float height = Float.parseFloat(groups[3]) * (float) origHeight;
      if (width > (origWidth - left)) {
        width = origWidth - left;
      }
      if (height > (origHeight - top)) {
        height = origHeight - top;
      }
      return new CropParameters((int) left, (int) top, (int) width, (int) height);
    }
  }

  public static ScaleParameters getScaleParameters(String targetSize, int origWidth, int origHeight)
      throws InvalidParametersException {
    double aspect = (double) origWidth / (double) origHeight;
    int width;
    int height;
    if (targetSize.endsWith(",")) {
      try {
        width = Integer.parseInt(targetSize.substring(0, targetSize.length() - 1));
      } catch (NumberFormatException e) {
        width =
            (int) (Float.parseFloat(targetSize.substring(0, targetSize.length() - 1)) * origWidth);
      }
      height = (int) ((double) width / aspect);
    } else if (targetSize.startsWith(",")) {
      try {
        height = Integer.parseInt(targetSize.substring(1));
      } catch (NumberFormatException e) {
        height = (int) (Float.parseFloat(targetSize.substring(1)) * origHeight);
      }
      width = (int) (aspect * (double) height);
    } else {
      String[] groups = targetSize.split(",");
      if (groups.length != 2) {
        throw new InvalidParametersException();
      }
      try {
        width = Integer.parseInt(groups[0]);
        height = Integer.parseInt(groups[1]);
      } catch (NumberFormatException e) {
        width = (int) (Float.parseFloat(groups[0]) * origWidth);
        height = (int) (Float.parseFloat(groups[1]) * origHeight);
      }
    }
    return new ScaleParameters(width, height);
  }

  public static BitDepths getQuality(String targetQuality) throws InvalidParametersException {
    if (targetQuality.equals("color")) {
      return BitDepths.COLOR;
    } else if (targetQuality.equals("grey")) {
      return BitDepths.GRAYSCALE;
    } else if (targetQuality.equals("bitonal")) {
      return BitDepths.BITONAL;
    } else {
      throw new InvalidParametersException();
    }
  }

  public static Formats getFormat(String targetFormat) throws UnsupportedFormat {
    if (targetFormat.equals("jpg") || targetFormat.equals("jpeg")) {
      return Formats.JPEG;
    } else if (targetFormat.equals("gif")) {
      return Formats.GIF;
    } else if (targetFormat.equals("png")) {
      return Formats.PNG;
    } else if (targetFormat.equals("tif") || targetFormat.equals("tiff")) {
      return Formats.TIF;
    } else if (targetFormat.equals("pdf")) {
      return Formats.PDF;
    } else {
      throw new UnsupportedFormat();
    }
  }
}
