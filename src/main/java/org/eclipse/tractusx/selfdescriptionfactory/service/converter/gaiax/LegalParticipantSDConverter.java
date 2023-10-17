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

package org.eclipse.tractusx.selfdescriptionfactory.service.converter.gaiax;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.SDFactory;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalParticipantSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.RegistrationNumberSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.converter.RegCodeMapper;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("gaia-x-ctx")
public class LegalParticipantSDConverter implements Converter<LegalParticipantSchema, SDFactory.SelfDescription> {

    private final CustodianWallet custodianWallet;
    private final Map<RegistrationNumberSchema.TypeEnum, String> regCodeMapper = RegCodeMapper.getRegCodeMapper("gx:");
    @Value("${app.verifiableCredentials.gaia-x-participant-schema}")
    private URI contextUri;


    @Override
    public SDFactory.SelfDescription convert(LegalParticipantSchema legalParticipantSchema) {
        var legalParticipantSD = new SDFactory.SelfDescription(List.of(contextUri), legalParticipantSchema.getHolder(), legalParticipantSchema.getIssuer(), legalParticipantSchema.getExternalId(), null);
        legalParticipantSD.put("@context", Map.of("ctxsd", "https://w3id.org/catena-x/core#"));
        legalParticipantSD.put("id", custodianWallet.getWalletData(legalParticipantSchema.getBpn()).get("did"));
        legalParticipantSD.put("type", "gx:LegalParticipant");
        legalParticipantSD.put("ctxsd:bpn", legalParticipantSchema.getBpn());
        legalParticipantSD.put("gx:legalName", custodianWallet.getWalletData(legalParticipantSchema.getBpn()).get("name"));
        legalParticipantSD.put(
                "gx:legalRegistrationNumber",
                legalParticipantSchema.getRegistrationNumber().stream().map(regNum -> Map.of(regCodeMapper.get(regNum.getType()), regNum.getValue())).toList()
        );
        legalParticipantSD.put("gx:headquarterAddress", Map.of("gx:countrySubdivisionCode", legalParticipantSchema.getHeadquarterAddressCountry()));
        legalParticipantSD.put("gx:legalAddress", Map.of("gx:countrySubdivisionCode", legalParticipantSchema.getLegalAddressCountry()));
        return legalParticipantSD;
    }
}
