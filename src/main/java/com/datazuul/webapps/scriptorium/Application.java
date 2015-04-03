package com.datazuul.webapps.scriptorium;

import com.datazuul.webapps.scriptorium.config.SpringConfig;
import de.codecentric.boot.admin.config.EnableAdminServer;
import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>Central application class. Normally you would add @EnableWebMvc for a Spring MVC app, but Spring Boot adds it
 * automatically when it sees spring-webmvc on the classpath. This flags the application as a web application and
 * activates key behaviors such as setting up a DispatcherServlet.</p>
 * 
 * <pre>mvn package && java -jar target/gs-spring-boot-0.1.0.jar</pre>
 * 
 * @author Ralf Eichinger <ralf.eichinger@alexandria.de>
 */
@Configuration
@EnableAdminServer
@EnableAutoConfiguration // tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings
@ComponentScan
@Import(SpringConfig.class)
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

}
