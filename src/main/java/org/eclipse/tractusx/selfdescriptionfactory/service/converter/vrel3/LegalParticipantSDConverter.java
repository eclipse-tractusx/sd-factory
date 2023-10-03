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

package org.eclipse.tractusx.selfdescriptionfactory.service.converter.vrel3;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.SDFactory;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalParticipantSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("catena-x-ctx")
public class LegalParticipantSDConverter implements Converter<LegalParticipantSchema, SDFactory.SelfDescription> {

    @Value("${app.verifiableCredentials.schema2210Url}")
    private URI contextUri;

    @Override
    public SDFactory.SelfDescription convert(LegalParticipantSchema legalParticipantSchema) {
        var legalParticipantSD =  new SDFactory.SelfDescription(List.of(contextUri), legalParticipantSchema.getHolder(), legalParticipantSchema.getIssuer(), legalParticipantSchema.getExternalId(), null);
        legalParticipantSD.put("type", "LegalParticipant");
        legalParticipantSD.put("bpn", legalParticipantSchema.getBpn());
        legalParticipantSD.put(
                "registrationNumber",
                legalParticipantSchema.getRegistrationNumber().stream()
                        .map(rNum -> {
                            var val = new LinkedHashMap<String, Object>();
                            val.put("type", rNum.getType());
                            val.put("value", rNum.getValue());
                            return val;
                        }).collect(Collectors.toUnmodifiableSet())
        );
        legalParticipantSD.put("headquarterAddress", Map.of("countryCode", legalParticipantSchema.getHeadquarterAddressCountry()));
        legalParticipantSD.put("legalAddress", Map.of("countryCode", legalParticipantSchema.getLegalAddressCountry()));
        return legalParticipantSD;
    }
}
