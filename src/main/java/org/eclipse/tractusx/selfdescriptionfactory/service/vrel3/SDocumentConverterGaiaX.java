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

package org.eclipse.tractusx.selfdescriptionfactory.service.vrel3;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalPersonSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@Profile("gaia-x-ctx")
@RequiredArgsConstructor
public class SDocumentConverterGaiaX extends SDocumentConverter implements Converter<SelfdescriptionPostRequest, Claims> {

    private final CustodianWallet custodianWallet;
    @Value("${app.verifiableCredentials.gaia-x-participant-schema}")
    private String gaiaxParticipantSchema;
    @Value("${app.verifiableCredentials.gaia-x-service-schema}")
    private String gaiaxServiceOfferingSchema;
    @Value("${app.verifiableCredentials.catena-x-schema}")
    private String catenaxSchema;

    @Override
    public Claims convert(@NonNull SelfdescriptionPostRequest source) {
        String externalId;
        Map<String, Object> converted;
        List<URI> contexts;
        if (source instanceof LegalPersonSchema lp) {
            externalId = lp.getExternalId();
            converted = legalPerson(lp);
            contexts = Stream.of(gaiaxParticipantSchema, catenaxSchema).map(URI::create).toList();
        } else if (source instanceof ServiceOfferingSchema so) {
            externalId = so.getExternalId();
            converted = serviceOffering(so);
            contexts = Stream.of(gaiaxServiceOfferingSchema, catenaxSchema).map(URI::create).toList();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "LegalPersonSchema is supported only"
            );
        }
        var withExternalId = new LinkedHashMap<>(converted);
        withExternalId.put("externalId", externalId);
        return new Claims(
                withExternalId,
                contexts
        );
    }

    private Map<String, Object> legalPerson(LegalPersonSchema legalPersonSchema) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", custodianWallet.getWalletData(legalPersonSchema.getBpn()).get("did"));
        res.put("type", legalPersonSchema.getType());
        res.put("ctxsd:bpn", legalPersonSchema.getBpn());
        res.put("gx-participant:name", custodianWallet.getWalletData(legalPersonSchema.getBpn()).get("name"));
        res.put(
                "gx-participant:registrationNumber",
                legalPersonSchema.getRegistrationNumber().stream().map(
                    regNum -> {
                        var regNumNode = new LinkedHashMap<String, Object>();
                        regNumNode.put("gx-participant:registrationNumberType", regNum.getType());
                        regNumNode.put("gx-participant:registrationNumberNumber", regNum.getValue());
                        return regNumNode;
                    }).toList()
        );
        return res;
    }

    private Map<String, Object> serviceOffering(ServiceOfferingSchema serviceOfferingSchema) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", custodianWallet.getWalletData(serviceOfferingSchema.getHolder()).get("did"));
        res.put("type", serviceOfferingSchema.getType());
        res.put("ctxsd:connector-url", "https://connector-placeholder.net");
        res.put("gx-service:providedBy", serviceOfferingSchema.getProvidedBy());
        Map<String, Object> dataAccountExportNode = new LinkedHashMap<>();
        dataAccountExportNode.put("gx-service:requestType", "email");
        dataAccountExportNode.put("gx-service:accessType", "digital");
        dataAccountExportNode.put("gx-service:formatType", "json");
        res.put("gx-service:dataAccountExport", List.of(dataAccountExportNode));
        var setter = new Object() {
            <T> Consumer<T> set(String fieldName) {
                return t -> res.put(fieldName, t);
            }
        };
        Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getAggregationOf(), Utils::uriFromStr).ifPresent(setter.set("gx-service:aggregationOf"));
        Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getTermsAndConditions(), this::getTermsAndConditions).ifPresent(setter.set("gx-service:termsAndConditions"));
        Utils.getNonEmptyListFromCommaSeparated(serviceOfferingSchema.getPolicies(), Function.identity()).ifPresent(setter.set("gx-service:policy"));
        return res;
    }
}

