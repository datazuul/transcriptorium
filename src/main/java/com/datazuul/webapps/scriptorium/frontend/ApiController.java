package com.datazuul.webapps.scriptorium.frontend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main controller.
 *
 * @author Ralf Eichinger <ralf.eichinger@alexandria.de>
 */
@RestController // @ApiController combines @Controller and @ResponseBody: the method returns pure text
@RequestMapping("/api")
public class ApiController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
