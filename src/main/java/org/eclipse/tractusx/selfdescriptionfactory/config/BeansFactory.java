package org.eclipse.tractusx.selfdescriptionfactory.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansFactory {

    @Bean
    ObjectMapper nonNullObjectMapper() {
        return JsonMapper.builder()
                .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
                .build().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
