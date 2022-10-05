/********************************************************************************
 * Copyright (c) 2021,2022 Catena-X
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.selfdescriptionfactory.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@OpenAPIDefinition
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

        Schema sdDocumentReq = new Schema<Map<String,Object>>()
                .addProperty("company_number",new StringSchema().example("123456"))
                .addProperty("headquarter_country",new StringSchema().example("DE"))
                .addProperty("legal_country",new StringSchema().example("DE"))
                .addProperty("service_provider",new StringSchema().example("http://www.test.d.com"))
                .addProperty("type",new StringSchema().example("connector"))
                .addProperty("bpn",new StringSchema().example("BPNL000000000000"))
                .addProperty("holder",new StringSchema().example("BPNL000000000000"))
                .addProperty("issuer",new StringSchema().example("did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"))
                .type("object");

        return new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
                .addSchemas("sdDocumentReq", sdDocumentReq);
    }

    @Bean
    public OpenAPI openApiConfig() {
        return new OpenAPI()
                .components(components())
                .info(apiInfo());
    }
}

