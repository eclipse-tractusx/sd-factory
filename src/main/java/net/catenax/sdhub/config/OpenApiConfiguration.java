package net.catenax.sdhub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                .title("SD-Hub API")
                .description("API for retrieving the Verifiable Credentials and Verifiable Presentation")
                .termsOfService("")
                .version(version);
    }


    @Bean
    public OpenAPI openApiConfig() {
        return new OpenAPI()
                .info(apiInfo());
    }
}

