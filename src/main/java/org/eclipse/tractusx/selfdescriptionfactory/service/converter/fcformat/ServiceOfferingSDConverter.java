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
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.converter.TermsAndConditionsHelper;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Profile("fc-ctx")
public class ServiceOfferingSDConverter implements Converter<ServiceOfferingSchema, SelfDescription> {

    private final CustodianWallet custodianWallet;
    private final TermsAndConditionsHelper termsAndConditionsHelper;

    @Override
    public SelfDescription convert(ServiceOfferingSchema serviceOfferingSchema) {


        var serviceOfferingSD = new SelfDescription(serviceOfferingSchema.getExternalId());

        var serviceOfferingVc = new LinkedHashMap<String, Object>();


        serviceOfferingVc.put("@context", Map.of(
                        "gx", "https://w3id.org/gaia-x/gax-trust-framework#",
                        "gax-core", "https://w3id.org/gaia-x/core#",
                        "ctxsd", "https://w3id.org/catena-x/core#",
                        "xsd", "http://www.w3.org/2001/XMLSchema#"
                )
        );
        serviceOfferingVc.put("@id", custodianWallet.getWalletData(serviceOfferingSchema.getHolder()).get("did"));
        serviceOfferingVc.put("@type", "gx:ServiceOffering");
        serviceOfferingVc.put("ctxsd:connector-url", "https://connector-placeholder.net");
        //WARNING! In Trust Framework specs it is called 'providedBy'
        //but in FC it is referred as 'gax-core:offeredBy'
        serviceOfferingVc.put("gax-core:offeredBy", Map.of(
                "@id", serviceOfferingSchema.getProvidedBy())
        );
        serviceOfferingVc.put("gx:dataAccountExport", Map.of(
                "gx:requestType", "email",
                "gx:accessType", "digital",
                "gx:formatType", "json")
        );
        var setter = new Object() {
            Consumer<Object> set(String fieldName) {
                return any -> serviceOfferingVc.put(fieldName, any);
            }
        };
        Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getAggregationOf(), Utils::uriFromStr)
                .map(l -> l.size() == 1 ? l.iterator().next() : l)
                .ifPresent(setter.set("gx:aggregationOf"));
        setter.set("gx:termsAndConditions").accept(
                Utils.getNonEmptyListFromCommaSeparated(
                                serviceOfferingSchema.getTermsAndConditions(),
                                url -> termsAndConditionsHelper.getTermsAndConditions(
                                        url,
                                        u -> Map.of("gx:content",
                                                Map.of("@type", "xsd:anyURI",
                                                        "@value", u
                                                )
                                        ),
                                        h -> Map.of("gx:hash", h)
                                )
                        ).map(l -> l.size() == 1 ? l.iterator().next() : l)
                        .orElse(Map.of(
                                "gx:content", Map.of(
                                        "@type", "xsd:anyURI",
                                        "@value", "http://example.org/tac-placeholder"),
                                "gx:hash", "1234")
                        )
        );
        setter.set("gx:policy").accept(
                Utils.getNonEmptyListFromCommaSeparated(
                                serviceOfferingSchema.getPolicies(),
                                Function.identity()
                        ).map(l -> l.size() == 1 ? l.iterator().next() : l)
                        .orElse("policy_placeholder")
        );

        var legalParticipantSD = VerifiableCredential.builder()
                .issuer(URI.create(serviceOfferingSchema.getIssuer()))
                .credentialSubject(CredentialSubject.fromMap(serviceOfferingVc))
                .build();
        serviceOfferingSD.getVerifiableCredentialList().add(legalParticipantSD);

        return serviceOfferingSD;
    }
}
