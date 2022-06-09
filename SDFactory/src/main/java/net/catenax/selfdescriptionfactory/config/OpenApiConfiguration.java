package net.catenax.selfdescriptionfactory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Value("${app.build.version}")
    private String version;

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("SD-Factory API")
                .description("API for creating and storing the Verifiable Credentials")
                .termsOfService("")
                .version(version);
    }

    @Bean
    Components components() {
        return new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                );
    }

    @Bean
    public OpenAPI openApiConfig() {
        return new OpenAPI()
                .components(components())
                .info(apiInfo());
    }
}

