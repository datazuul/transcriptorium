package com.datazuul.webapps.scriptorium.config;

import com.datazuul.webapps.scriptorium.domain.ImageFactory;
import com.datazuul.webapps.scriptorium.domain.JAIImageFactory;
import com.datazuul.webapps.scriptorium.frontend.ImageController;
import com.datazuul.webapps.scriptorium.frontend.resolver.FileSystemResolver;
import com.datazuul.webapps.scriptorium.frontend.resolver.HttpResolver;
import com.datazuul.webapps.scriptorium.frontend.resolver.ImageResolver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

  @Value("${app.httpRepository.baseUrl}")
  private String httpRepositoryBaseUrl;

  @Value("${app.uploadFolder.path}")
  private String uploadFolderPath;

  //    @Bean
  public ImageController getImageController() {
    List<ImageResolver> resolvers = new LinkedList<>();
    // Resolve bavarikon IDs without datastream IDs to the bavarikon Fedora URL of the ZOOM
    // datastream
    resolvers.add(
        new HttpResolver(
            "^(?:alx:)?([A-Z]{3}-[A-Z]{3}-\\w{16})$",
            httpRepositoryBaseUrl + "/objects/alx:$1/datastreams/ZOOM/content"));
    // Resolve bavarikon IDs with datastream IDs to bavarikon Fedora URLs
    resolvers.add(
        new HttpResolver(
            "^(?:alx:)?([A-Z]{3}-[A-Z]{3}-\\w{16})@([\\w\\-]+)$",
            httpRepositoryBaseUrl + "/objects/alx:$1/datastreams/$2/content"));
    // Resolve upload file IDs to filesystem path
    String basePath = uploadFolderPath.replace("~", System.getProperty("user.home"));
    resolvers.add(new FileSystemResolver("^upload(\\d+)$", String.format("%s/$1", basePath)));
    //        List<ImageFactory> imageFactories = Arrays.asList(
    //                new JpegTranImageFactory(),
    //                new JAIImageFactory()
    //        );
    List<ImageFactory> imageFactories = new ArrayList<>();
    imageFactories.add(new JAIImageFactory());

    ImageController controller = new ImageController();
    //        controller.setFullEnabled(true);
    //        controller.setRefererCheckEnabled(false);
    controller.setImageFactories(imageFactories);
    controller.setResolvers(resolvers);
    return controller;
  }

  @Bean
  public LayoutDialect layoutDialect() {
    return new LayoutDialect();
  }
}
