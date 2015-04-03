package com.datazuul.webapps.scriptorium.domain;

import com.datazuul.webapps.scriptorium.domain.exceptions.InvalidParametersException;
import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedFormat;
import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedOperation;
import com.datazuul.webapps.scriptorium.domain.parameters.CropParameters;
import com.datazuul.webapps.scriptorium.domain.parameters.ScaleParameters;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.imgscalr.Scalr;

public class JAIImage implements Image {
    private BufferedImage image;
    private String formatString;

    public JAIImage(byte[] inData) throws IOException, UnsupportedFormat {
        this(new ByteArrayInputStream(inData));
    }
    public JAIImage(InputStream imgData) throws IOException, UnsupportedFormat {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(imgData));
        if (readers.hasNext()) {
            this.formatString = readers.next().getFormatName();
        } else {
            throw new UnsupportedFormat("Could not read image, unsupported format?");
        }
        imgData.reset();
        BufferedImage img = ImageIO.read(imgData);
        this.image = img;
    }

    public static Formats getFormatFromString(String formatName) throws UnsupportedFormat {
        formatName = formatName.toLowerCase();
        if (formatName.equals("jpeg") || formatName.equals("jpg")) {
            return Formats.JPEG;
        } else if (formatName.equals("png")) {
            return Formats.PNG;
        } else if (formatName.equals("gif")) {
            return Formats.GIF;
        } else if (formatName.equals("jpeg2000")) {
            return Formats.JP2;
        } else if (formatName.equals("tif")) {
            return Formats.TIF;
        } else {
            throw new UnsupportedFormat();
        }
    }

    @Override
    public Formats getFormat() {
        try {
            return getFormatFromString(this.formatString);
        } catch (UnsupportedFormat ignored) {
            // NOTE: This should never happen
            return null;
        }
    }
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(this.image, formatString, os);
        byte[] output = os.toByteArray();
        os.close();
        return output;
    }

    @Override
    public Image crop(CropParameters params) throws UnsupportedOperation, InvalidParametersException {
        BufferedImage dest = image.getSubimage(params.getHorizontalOffset(), params.getVerticalOffset(),
                params.getWidth(), params.getHeight());
        this.image = dest;
        return this;
    }

    @Override
    public Image scale(ScaleParameters params) throws UnsupportedOperation, InvalidParametersException {
        int oldWidth = getWidth();
        int oldHeight = getHeight();
        if (oldWidth == params.getTargetWidth() && oldHeight == params.getTargetHeight()) {
            return this;
        }
        this.image = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_EXACT, params.getTargetWidth(), params.getTargetHeight());
        return this;
    }

    @Override
    public Image rotate(int arcDegree) throws UnsupportedOperation, InvalidParametersException {
        if (arcDegree % 90 > 0) {
            throw new UnsupportedOperation("Can only rotate by multiples of 90 degrees.");
        }
        Scalr.Rotation rotation;
        switch(arcDegree) {
            case -90:
            case 270:
                rotation = Scalr.Rotation.CW_270;
                break;
            case 90:
                rotation = Scalr.Rotation.CW_90;
                break;
            case 180:
                rotation = Scalr.Rotation.FLIP_VERT;
                break;
            default:
                return this;
        }
        /*
        BufferedImage dest = new BufferedImage(targetWidth, targetHeight, image.getType());
        AffineTransform at = new AffineTransform();
        at.rotate(radianDegree, getWidth()/2, getHeight()/2);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        this.image = rotateOp.filter(image, dest);
        */
        this.image = Scalr.rotate(image, rotation);
        return this;
    }

    @Override
    public Image toDepth(BitDepths depth) throws UnsupportedOperation {
        throw new UnsupportedOperation("Unsupported Operation");
    }

    @Override
    public Image convert(Formats format) throws UnsupportedOperation {
        if (format == Formats.JPEG) {
            this.formatString = "JPEG";
        } else if (format == Formats.PNG) {
            this.formatString = "PNG";
        } else {
            throw new UnsupportedOperation("Only JPEG and PNG images are supported.");
        }
        return this;
    }
}
