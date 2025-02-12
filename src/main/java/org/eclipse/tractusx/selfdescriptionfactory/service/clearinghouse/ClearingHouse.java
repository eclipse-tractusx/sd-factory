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

package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import foundation.identity.jsonld.JsonLDObject;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.config.TechnicalUsersDetails;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearingHouse implements InitializingBean {

    private final TechnicalUsersDetails technicalUsersDetails;
    private final ClearingHouseClient clearingHouseClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment environment;

    public void sendToClearingHouse(JsonLDObject payload, String externalId) {
        ObjectNode payloadObject = objectMapper.convertValue(payload, ObjectNode.class);
        payloadObject.put("@context", payloadObject.get("@context").get(0).asText());
        payloadObject.put("type", payloadObject.get("type").get(0).asText());

        if (log.isDebugEnabled()) {
            debug(payloadObject,  externalId);
        }
        if (!Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            clearingHouseClient.send(payloadObject, externalId);
        }
    }

    @SneakyThrows
    protected void debug(ObjectNode payload, String externalId) {
        var annotation = ClearingHouseClient.class.getAnnotation(FeignClient.class);
        var name = annotation.name();
        Optional.ofNullable(technicalUsersDetails.getUsersDetails().get(name)).map(TechnicalUsersDetails.UserDetail::uri).ifPresent(uri -> log.debug("URL: {}", uri));
        log.debug("ExternalId: {}", externalId);
        log.debug("payload: {}", objectMapper.writeValueAsString(payload));
    }

    @Override
    public void afterPropertiesSet() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
