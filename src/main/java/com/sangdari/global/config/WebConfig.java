package com.sangdari.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String storageFilesLocation;

    public WebConfig(
            @Value("${storage.files.location}") String storageFilesLocation
    ) {
        this.storageFilesLocation = storageFilesLocation;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = Path.of(storageFilesLocation)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry.addResourceHandler("/files/**")
                .addResourceLocations(resourceLocation);
    }
}
