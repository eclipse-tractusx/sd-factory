/********************************************************************************
 * Copyright (c) 2024 T-Systems International GmbH
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("jsonld", new MediaType("application", "ld+json"))
                .mediaType("yml", new MediaType("application", "yaml"))
                .mediaType("yaml", new MediaType("application", "yaml"))
                .mediaType("yml", new MediaType("text", "yaml"))
                .mediaType("yaml", new MediaType("text", "yaml"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/context/**") // URL pattern for static resources
                .addResourceLocations("classpath:/verifiablecredentials/") // Location of static resources
                .setCachePeriod(3600) // Cache period in seconds (optional)
                .resourceChain(true) // Enable resource chain optimization (optional)
                .addResolver(new CustomResolver());
    }

    private static class CustomResolver implements ResourceResolver {

        /**
         * Resolves the requested resource based on the provided request path and locations using the given chain.
         * If the resource is not found in the provided locations, falls back to the chain to resolve it.
         *
         * @param request the HttpServletRequest object
         * @param requestPath the path of the requested resource
         * @param locations the list of possible resource locations to search in
         * @param chain the ResourceResolverChain to delegate resource resolution if needed
         * @return the resolved resource or null if not found
         */
        @Override
        @Nullable
        public Resource resolveResource(@Nullable HttpServletRequest request, String requestPath,
                                        List<? extends Resource> locations, ResourceResolverChain chain) {
            // Modify the requestPath to remove any trailing "/"
            var requestPathModified = requestPath.endsWith("/") ? requestPath.substring(0, requestPath.length() - 1) : requestPath;
            // Attempt to resolve the resource in each location or with a ".jsonld" extension
            return locations.stream()
                    .flatMap(location -> Optional.ofNullable(resolveResource(location, requestPathModified))
                            .or(() -> Optional.ofNullable(resolveResource(location, requestPathModified.concat(".jsonld"))))
                            .stream()
                    ).findAny()
                    .orElseGet(() -> chain.resolveResource(request, requestPath, locations));
        }
        private Resource resolveResource(Resource location, String requestPath) {
            return Try.of(() -> location.createRelative(requestPath))
                    .filter(resolvedResource ->resolvedResource.exists() && resolvedResource.isReadable())
                    .getOrNull();
        }

        @Override
        @Nullable
        public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
            return Try.ofSupplier(() -> resolveResource(null, resourcePath, locations, chain))
                    .mapTry(Resource::getURL)
                    .map(Objects::toString).getOrNull();
        }
    }
}
