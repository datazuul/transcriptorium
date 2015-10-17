package com.datazuul.webapps.scriptorium.frontend;

import com.datazuul.webapps.scriptorium.domain.Formats;
import com.datazuul.webapps.scriptorium.domain.Image;
import com.datazuul.webapps.scriptorium.domain.ImageFactory;
import com.datazuul.webapps.scriptorium.domain.exceptions.ResolvingException;
import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedFormat;
import com.datazuul.webapps.scriptorium.domain.exceptions.UnsupportedOperation;
import com.datazuul.webapps.scriptorium.frontend.params.ParamParser;
import com.datazuul.webapps.scriptorium.frontend.resolver.ImageResolver;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Serves images
 *
 * @author Ralf Eichinger <ralf.eichinger@alexandria.de>
 */
@Controller
//@RequestMapping("/img")
@SessionAttributes(value = {"images", "directory"})
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private List<ImageFactory> imageFactories = Collections.emptyList();
//    private RedisTemplate redisTemplate;
    private List<ImageResolver> resolvers = Collections.emptyList();

    @ModelAttribute("images")
    public List<Path> getUserObject() {
        return new ArrayList<>();
    }

    @RequestMapping(value = {"/edit"}, method = RequestMethod.GET)
    String index() {
        return "index";
    }
    
    @RequestMapping(value = {"/edit"}, method = RequestMethod.POST)
    public String loadDirectory(@RequestParam String directory, @ModelAttribute("images") List<Path> images, Model model) throws ResolvingException, URISyntaxException {
        directory = cleanPath(directory);
        if ((images.isEmpty()) && directory != null) {
            Path imageDirectory = Paths.get(new URI("file:" + directory));
            final List<Path> imageFiles = new ArrayList<>();
            try {
                Files.walkFileTree(imageDirectory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (!attrs.isDirectory()) {
                            // TODO there must be a more elegant solution for filtering jpeg files...
                            if (file.getFileName().toString().endsWith("jpg")) {
                                imageFiles.add(file);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            Collections.sort(imageFiles, new Comparator() {
                @Override
                public int compare(Object fileOne, Object fileTwo) {
                    String filename1 = ((Path) fileOne).getFileName().toString();
                    String filename2 = ((Path) fileTwo).getFileName().toString();

                    try {
                        Integer number1 = Integer.parseInt(filename1.substring(0, filename1.lastIndexOf(".")));
                        Integer number2 = Integer.parseInt(filename2.substring(0, filename2.lastIndexOf(".")));
                        return number1.compareTo(number2);
                    } catch (NumberFormatException nfe) {
                        return filename1.compareToIgnoreCase(filename2);
                    }
                }
            });
            images = imageFiles;
            model.addAttribute("images", images);
            model.addAttribute("directory", directory);
        }
        return "index";
    }

    @RequestMapping(value = {"/img/{index}"}, method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImageRepresentation(@PathVariable int index, @ModelAttribute("images") List<Path> images) throws ResolvingException, URISyntaxException {
        try {
            Path filePath = images.get(index);
            byte[] readAllBytes = null;
            try {
                readAllBytes = Files.readAllBytes(filePath);
            } catch (IOException e) {
                throw new ResolvingException(filePath.toString(), e);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(readAllBytes, headers, HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @RequestMapping(value = {"/img/{identifier}.{format}"})
    public ResponseEntity<byte[]> getImageRepresentation(
            @PathVariable String identifier,
            @PathVariable String format,
            HttpServletRequest request) throws UnsupportedFormat, UnsupportedOperation, IOException, ResolvingException {
        Formats targetFormat = ParamParser.getFormat(format);
        HttpHeaders headers = new HttpHeaders();

        Image image = getImage(identifier);
        if (!(targetFormat == image.getFormat())) {
            image = image.convert(targetFormat);
            headers.setContentType(getMediaType(targetFormat));
        } else {
            headers.setContentType(getMediaType(Formats.JPEG));
        }
        return new ResponseEntity<>(image.toByteArray(), headers, HttpStatus.OK);
    }

    private MediaType getMediaType(Formats format) throws UnsupportedFormat {
        switch (format) {
            case JPEG:
                return MediaType.IMAGE_JPEG;
            case PNG:
                return MediaType.IMAGE_PNG;
            case GIF:
                return MediaType.IMAGE_GIF;
            default:
                throw new UnsupportedFormat("Unsupported output image format.");
        }
    }

    public Image getImage(String identifier) throws ResolvingException,
            UnsupportedFormat, IOException {
        byte[] imageData = getImageData(identifier);
        for (ImageFactory factory : imageFactories) {
            try {
                return factory.getImage(imageData);
            } catch (UnsupportedFormat ignored) {
            }
        }
        throw new UnsupportedFormat("Input image format is not supported.");
    }

    private byte[] getImageData(String identifier) throws ResolvingException {
//        String cacheKey = getCacheKey(identifier);
//        if (redisTemplate != null && redisTemplate.hasKey(cacheKey)) {
//            return (byte[]) redisTemplate.boundValueOps(cacheKey).get();
//        }

        LOGGER.debug("Try to resolve: " + identifier);

        byte[] imageData = null;
        for (ImageResolver resolver : resolvers) {
            try {
                if (resolver.isResolvable(identifier)) {
                    imageData = resolver.getImageData(identifier);
                } else {
                    throw new ResolvingException(identifier
                            + " cannot be resolved with this resolver: "
                            + resolver.getClass().getName());
                }
                if (imageData.length > 0) {
                    break;
                }
            } catch (ResolvingException ignored) {
                LOGGER.debug(ignored.getMessage());
            }
        }
        if (imageData == null || imageData.length == 0) {
            throw new ResolvingException(
                    "Could not find a resolver for the identifier: "
                    + identifier);
        }

//        if (redisTemplate != null) {
//            redisTemplate.boundValueOps(cacheKey).set(imageData, 1,
//                    TimeUnit.DAYS);
//        }
        return imageData;
    }

    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = getFromHeader(request, "X-Forwarded-For");
        if (ipUnknown(ip)) {
            ip = getFromHeader(request, "Proxy-Client-IP");
        }
        if (ipUnknown(ip)) {
            ip = getFromHeader(request, "WL-Proxy-Client-IP");
        }
        if (ipUnknown(ip)) {
            ip = getFromHeader(request, "HTTP_CLIENT_IP");
        }
        if (ipUnknown(ip)) {
            ip = getFromHeader(request, "HTTP_X_FORWARDED_FOR");
        }
        if (ipUnknown(ip)) {
            ip = request.getRemoteAddr();
            LOGGER.debug("Found IP {} by using getRemoteAddr().", ip);
        }
        return ip;
    }

    private static String getFromHeader(HttpServletRequest request,
            String headerKey) {
        final String ip = request.getHeader(headerKey);
        if (!ipUnknown(ip)) {
            LOGGER.debug("Found IP {} in header {}.", ip, headerKey);
        }
        return ip;
    }

    private static boolean ipUnknown(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    public void setImageFactories(List<ImageFactory> imageFactories) {
        this.imageFactories = imageFactories;
    }

    public void setResolvers(List<ImageResolver> resolvers) {
        this.resolvers = resolvers;
    }

    private String cleanPath(String directory) {
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        return directory;
    }
}
