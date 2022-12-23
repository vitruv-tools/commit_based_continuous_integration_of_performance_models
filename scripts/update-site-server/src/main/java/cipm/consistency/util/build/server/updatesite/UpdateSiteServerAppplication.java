package cipm.consistency.util.build.server.updatesite;

import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "cipm.consistency.util.build.server.updatesite")
public class UpdateSiteServerAppplication implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String jamoppUpdateSite = Paths.get("..", "..", "Palladio-Supporting-EclipseJavaDevelopmentTools", "releng",
            "org.palladiosimulator.jdt.updatesite", "target", "repository").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/jamopp/**").addResourceLocations(jamoppUpdateSite);
        String somoxUpdateSite = Paths.get("..", "..", "Palladio-ReverseEngineering-SoMoX-JaMoPP", "releng",
            "org.somox.updatesite", "target", "repository").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/somox/**").addResourceLocations(somoxUpdateSite);
    }

    public static void main(String[] args) {
        SpringApplication.run(UpdateSiteServerAppplication.class);
    }
}
