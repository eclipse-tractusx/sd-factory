/********************************************************************************
 * Copyright (c) 2021,2022 T-Systems International GmbH
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

package org.eclipse.tractusx.selfdescriptionfactory.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import feign.Feign;
import feign.Target;
import org.eclipse.tractusx.selfdescriptionfactory.service.keycloak.KeycloakClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
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

    @Bean
    public KeycloakClient keycloakClient(ObjectFactory<HttpMessageConverters> converters){
        return Feign.builder()
                .decoder(new SpringDecoder(converters))
                .target(Target.EmptyTarget.create(KeycloakClient.class));
    }
}
