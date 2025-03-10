/********************************************************************************
 * Copyright (c) 2022,2025 T-Systems International GmbH
 * Copyright (c) 2022,2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.service.keycloak.KeycloakManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Configuration
@Slf4j
public class DefaultFeignConfig {
    @Bean
    public RequestInterceptor getRequestInterceptor(KeycloakManager keycloakManager, TechnicalUsersDetails technicalUsersDetails) {
        return  requestTemplate -> {
            Optional.of(requestTemplate.feignTarget().name())
                    .map(keycloakManager::getToken)
                    .map("Bearer "::concat)
                    .ifPresent(token -> requestTemplate.header("Authorization", token));
            Optional.of(requestTemplate.feignTarget().name())
                    .map(technicalUsersDetails.getUsersDetails()::get)
                    .map(TechnicalUsersDetails.UserDetail::uri)
                    .ifPresent(requestTemplate::target);
        };
    }

    @Bean
    public ErrorDecoder getErrorDecoder(ObjectMapper mapper) {
        return (methodKey, response) -> {
            String contentType = response.headers().getOrDefault("Content-Type", List.of("Unknown"))
                    .stream().findFirst()
                    .orElse("Unknown");
            var responseStr = Try.of(() -> response.body().asInputStream().readAllBytes()).map(bytes -> new String(bytes, response.charset())).getOrElse("");
            var msg = (contentType.contains("json") ? Try.success(new StringReader(responseStr)) : Try.<StringReader>failure(new NoSuchElementException()))
                        .mapTry(mapper::readTree)
                        .recover(any -> mapper.createObjectNode())
                        .map(jsonNode -> jsonNode.get("message"))
                        .filter(Objects::nonNull)
                        .map(JsonNode::asText)
                        .getOrElse(responseStr);
            var statusCode = HttpStatusCode.valueOf(response.status());
            log.error("Error in Feign client: {}", msg);
            log.error("Status code: {}", statusCode);
            log.error("URL: {}", response.request().url());
            if (response.request().body() != null) {
                log.error("Original payload: {}", new String(response.request().body(), response.request().charset()));
            }
            if(statusCode.isSameCodeAs(HttpStatusCode.valueOf(400)) && msg.contains("E2010")) {
                return new ResponseStatusException(HttpStatusCode.valueOf(202), methodKey.concat(" : ").concat(msg));
            }
            return new ResponseStatusException(statusCode, methodKey.concat(" : ").concat(msg));
        };
    }
}
