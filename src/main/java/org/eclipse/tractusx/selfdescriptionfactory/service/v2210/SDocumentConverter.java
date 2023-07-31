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

package org.eclipse.tractusx.selfdescriptionfactory.service.v2210;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
@Profile("catena-x-ctx")
public class SDocumentConverter implements Converter<SelfdescriptionPostRequest, Claims> {
    private final ObjectMapper objectMapper;
    @Value("${app.verifiableCredentials.schema2210Url}")
    private String schemaUrl;

    @Override
    public @NonNull Claims convert(@NonNull SelfdescriptionPostRequest source) {
        var sdoc2210Map = objectMapper.convertValue(source, new TypeReference<LinkedHashMap<String, Object>>(){});
        if (!sdoc2210Map.containsKey("bpn") && source instanceof ServiceOfferingSchema so) {
            sdoc2210Map.put("bpn", so.getHolder());
        }
        return new Claims(
                sdoc2210Map,
                Collections.singletonList(URI.create(schemaUrl))
        );
    }
}
