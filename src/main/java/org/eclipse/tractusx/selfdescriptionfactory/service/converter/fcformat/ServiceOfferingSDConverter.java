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

package org.eclipse.tractusx.selfdescriptionfactory.service.converter.fcformat;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.SDFactory;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.converter.TermsAndConditionsHelper;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("fc-ctx")
public class ServiceOfferingSDConverter implements Converter<ServiceOfferingSchema, SDFactory.SelfDescription> {

    private final CustodianWallet custodianWallet;
    private final TermsAndConditionsHelper termsAndConditionsHelper;

    @Override
    public SDFactory.SelfDescription convert(ServiceOfferingSchema serviceOfferingSchema) {
        /*
        var serviceOfferingSD = new ServiceOfferingSD();
        serviceOfferingSD.put("@context", ImmutableMap.of(
                "gx", "https://w3id.org/gaia-x/gax-trust-framework#",
                "gax-core", "https://w3id.org/gaia-x/core#",
                "ctxsd", "https://w3id.org/catena-x/core#",
                "xsd", "http://www.w3.org/2001/XMLSchema#"
                )
        );
        serviceOfferingSD.put("@id", custodianWallet.getWalletData(serviceOfferingSchema.getHolder()).get("did"));
        serviceOfferingSD.put("@type", "gx:ServiceOffering");
        serviceOfferingSD.put("ctxsd:connector-url", "https://connector-placeholder.net");
        //WARNING! In Trust Framework specs it is called 'providedBy'
        //but in FC it is referred as 'gax-core:offeredBy'
        serviceOfferingSD.put("gax-core:offeredBy", ImmutableMap.of(
                "@id", serviceOfferingSchema.getProvidedBy())
        );
        serviceOfferingSD.put("gx:dataAccountExport", ImmutableMap.of(
                "gx:requestType", "email",
                "gx:accessType", "digital",
                "gx:formatType", "json")
        );
        var setter = new Object() {
            Consumer<Object> set(String fieldName) {
                return any -> serviceOfferingSD.put(fieldName, any);
            }
        };
        Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getAggregationOf(), Utils::uriFromStr)
                .map(l -> l.size() == 1 ? l.iterator().next() : l)
                .ifPresent(setter.set("gx:aggregationOf"));
        setter.set("gx:termsAndConditions").accept(
                Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getTermsAndConditions(), url -> termsAndConditionsHelper.getTermsAndConditions(url, "gx:"))
                    .map(l -> l.size() == 1 ? l.iterator().next() : l)
                    .map(Object.class::cast)
                    .orElse(ImmutableMap.of(
                            "gx:content", ImmutableMap.of(
                                    "@type", "xsd:anyURI",
                                    "@value", "http://example.org/tac-placeholder"),
                            "gx:hash", "1234")
                    )
        );
        setter.set("gx:policy").accept(
                Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getPolicies(), Function.identity())
                        .map(Object.class::cast)
                        .orElse("policy_placeholder")
        );
        return serviceOfferingSD;

         */
        return null;
    }
}
