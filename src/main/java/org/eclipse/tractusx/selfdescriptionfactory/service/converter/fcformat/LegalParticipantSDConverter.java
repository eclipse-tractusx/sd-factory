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

package org.eclipse.tractusx.selfdescriptionfactory.service.converter.fcformat;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.SelfDescription;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalParticipantSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.*;

@Component
@RequiredArgsConstructor
@Profile("fc-ctx")
public class LegalParticipantSDConverter implements Converter<LegalParticipantSchema, SelfDescription> {

    private final CustodianWallet custodianWallet;

    @Override
    public SelfDescription convert(LegalParticipantSchema legalParticipantSchema) {
        if (legalParticipantSchema.getRegistrationNumber().size() != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Registration Number must have single element for Federated Catalog"
            );
        }
        var selfDescription = new SelfDescription(legalParticipantSchema.getExternalId());

        var legalParticipantVc = new LinkedHashMap<String, Object>();

        legalParticipantVc.put("@context", Map.of(
                "gx", "https://w3id.org/gaia-x/gax-trust-framework#",
                "xsd", "http://www.w3.org/2001/XMLSchema#",
                "vcard", "http://www.w3.org/2006/vcard/ns#",
                "ctxsd", "https://w3id.org/catena-x/core#")
        );
        legalParticipantVc.put("@id", custodianWallet.getWalletData(legalParticipantSchema.getHolder()).get("did"));
        legalParticipantVc.put("@type", "gx:LegalPerson");
        legalParticipantVc.put("ctxsd:bpn", legalParticipantSchema.getBpn());
        legalParticipantVc.put("gx:name", custodianWallet.getWalletData(legalParticipantSchema.getBpn()).get("name"));
        legalParticipantVc.put("gx:registrationNumber", legalParticipantSchema.getRegistrationNumber().iterator().next().getValue());
        legalParticipantVc.put("gx:headquarterAddress",
                Map.of("@type", "vcard:Address",
                        "vcard:country-name", legalParticipantSchema.getHeadquarterAddressCountry()
                )
        );
        legalParticipantVc.put("gx:legalAddress",
                Map.of("@type", "vcard:Address",
                        "vcard:country-name", legalParticipantSchema.getLegalAddressCountry()
                )
        );

       var legalParticipantSD = VerifiableCredential.builder()
               .issuer(URI.create(legalParticipantSchema.getIssuer()))
               .credentialSubject(CredentialSubject.fromMap(legalParticipantVc))
               .build();

        selfDescription.getVerifiableCredentialList().add(legalParticipantSD);
        return selfDescription;
    }
}
