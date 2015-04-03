package com.datazuul.webapps.scriptorium.frontend.resolver;

import com.datazuul.webapps.scriptorium.domain.exceptions.ResolvingException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

public class HttpResolver extends PatternResolver {

    private boolean forceJpeg;
    private Executor httpExecutor;

    public HttpResolver(String pattern, String replacement) {
        super(pattern, replacement);
        httpExecutor = Executor.newInstance();
    }

    public HttpResolver(Pattern pattern, String replacement) {
        super(pattern, replacement);
        httpExecutor = Executor.newInstance();
    }

    private byte[] convertToJpeg(byte[] data) throws IOException {
        if ((data[0] & 0xFF) != 0xFF || (data[1] & 0xFF) != 0xD8) {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "JPEG", os);
            return os.toByteArray();
        } else {
            return data;
        }
    }

    /**
     * Force the resolver to only return JPEG data. This causes getImageData to convert any non-JPEG images
     *
     * @param force
     */
    public void setForceJpeg(boolean force) {
        this.forceJpeg = force;
    }

    @Override
    public byte[] getImageData(String identifier) throws ResolvingException {
        String url = getSourcePath(identifier);
        try {
            byte[] data = retrieveData(url);
            if (forceJpeg) {
                data = convertToJpeg(data);
            }
            return data;
        } catch (IOException e) {
            throw new ResolvingException(url, e);
        }
    }

    protected byte[] retrieveData(String url) throws IOException {
        return httpExecutor.execute(Request.Get(url)).returnContent().asBytes();
    }

    public void setAuthCredentials(String userName, String password, String hostname) {
        httpExecutor.auth(new HttpHost(hostname), userName, password);
    }
}
