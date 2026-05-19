package com.example.demo.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.mascotas-dir:src/main/resources/static/img}")
    private String mascotasDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")          // Aplica a todas las rutas /api/...
                .allowedOrigins("http://localhost:4200") // Origen del frontend Angular
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/img/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/img/**", config);
        return source;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path directorio = Paths.get(mascotasDir).toAbsolutePath().normalize();
        String fileLocation = directorio.toUri().toString();
        registry.addResourceHandler("/img/**")
                .addResourceLocations(fileLocation, "classpath:/static/img/")
                .setCachePeriod(0);
    }
}