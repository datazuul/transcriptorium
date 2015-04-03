package com.datazuul.webapps.scriptorium.frontend;

import com.datazuul.webapps.scriptorium.domain.exceptions.ResolvingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for serving webpages.
 *
 * @author Ralf Eichinger <ralf.eichinger@alexandria.de>
 */
@Controller
class HtmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlController.class);

    @RequestMapping("/edit")
    String index() {
        return "index";
    }

    @ResponseBody
    @RequestMapping(value = {"/text/{identifier}"}, method = RequestMethod.GET)
    public String getText(@RequestParam String directory, @PathVariable String identifier) throws
            ResolvingException, IOException {
        String inputFilePath = directory + "/image-" + identifier;
        String html;
        if (Files.exists(Paths.get(inputFilePath + ".html"))) {
            inputFilePath = inputFilePath + ".html";
        } else {
            inputFilePath = inputFilePath + ".txt";
        }
        html = readFile(inputFilePath);
        return html;
    }

    @RequestMapping(value = {"/text/{identifier}"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveText(@RequestParam String directory, @RequestParam String id, @RequestParam String html, @PathVariable String identifier) throws IOException {
        final String outputFilePath = directory + "/image-" + identifier + ".html";
        LOGGER.info("saving to " + outputFilePath);
        if (html.contains("<body>")) {
            html = html.substring(html.indexOf("<body>" + 6), html.indexOf("</body>"));
        }
        writeFile(outputFilePath, html);
    }

    private String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String html = new String(encoded, StandardCharsets.UTF_8);
        if (path.endsWith(".txt")) {
            html = wrapIntoHtml(html);
        }
        return html;
    }

    private String wrapIntoHtml(String html) {
        html = "<!DOCTYPE html>\n"
                + "\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
                + "<head>\n"
                + "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
                + "</head>   \n"
                + "<body>" + html + "</body>"
                + "</html>";
        return html;
    }

    private void writeFile(String path, String content)
            throws IOException {
        if (!content.contains("<body>")) {
            content = wrapIntoHtml(content);
        }
        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        Files.write(Paths.get(path), bytes);
    }
}
