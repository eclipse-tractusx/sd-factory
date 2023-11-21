/********************************************************************************
 * Copyright (c) 2022,2023 T-Systems International GmbH
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory.service.converter.fcformat;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.SDFactory;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalParticipantSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Profile("fc-ctx")
public class LegalParticipantSDConverter implements Converter<LegalParticipantSchema, SDFactory.SelfDescription> {

    private final CustodianWallet custodianWallet;

    @Override
    public SDFactory.SelfDescription convert(@NonNull LegalParticipantSchema legalParticipantSchema) {
        if (legalParticipantSchema.getRegistrationNumber().size() != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Registration Number must have single element for Federated Catalog"
            );
        }
        var legalParticipantSD = new SDFactory.SelfDescription(List.of(), legalParticipantSchema.getHolder(), legalParticipantSchema.getIssuer(), legalParticipantSchema.getExternalId(), null);
        legalParticipantSD.put("@context", Map.of(
                "gx", "https://w3id.org/gaia-x/gax-trust-framework#",
                "xsd", "http://www.w3.org/2001/XMLSchema#",
                "vcard", "http://www.w3.org/2006/vcard/ns#",
                "ctxsd", "https://w3id.org/catena-x/core#")
        );
        legalParticipantSD.put("@id", custodianWallet.getWalletData(legalParticipantSchema.getHolder()).get("did"));
        legalParticipantSD.put("@type", "gx:LegalPerson");
        legalParticipantSD.put("ctxsd:bpn", legalParticipantSchema.getBpn());
        legalParticipantSD.put("gx:name", custodianWallet.getWalletData(legalParticipantSchema.getBpn()).get("name"));
        legalParticipantSD.put("gx:registrationNumber", legalParticipantSchema.getRegistrationNumber().iterator().next().getValue());
        legalParticipantSD.put("gx:headquarterAddress",
                Map.of("@type", "vcard:Address",
                        "vcard:country-name", legalParticipantSchema.getHeadquarterAddressCountry()
                )
        );
        legalParticipantSD.put("gx:legalAddress",
                Map.of("@type", "vcard:Address",
                        "vcard:country-name", legalParticipantSchema.getLegalAddressCountry()
                )
        );
        return legalParticipantSD;
    }
}
