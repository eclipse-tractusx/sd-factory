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

package org.eclipse.tractusx.selfdescriptionfactory.service.vrel3;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.AddressSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.DataAccountExportSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalParticipantSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("catena-x-ctx")
public class SDocumentConverterCatenaX extends SDocumentConverter implements Converter<SelfdescriptionPostRequest, Claims> {
    private final ObjectMapper objectMapper;
    private final org.eclipse.tractusx.selfdescriptionfactory.service.v2210.SDocumentConverter converter2210;
    private final org.eclipse.tractusx.selfdescriptionfactory.service.Validator validator;

    @Override
    public Claims convert(@NonNull SelfdescriptionPostRequest source) {
        String externalId;
        org.eclipse.tractusx.selfdescriptionfactory.model.v2210.SelfdescriptionPostRequest converted2210;
        if (source instanceof LegalParticipantSchema lp) {
            externalId = lp.getExternalId();
            converted2210 = validator.validated(this::convertRel3LegalParticipant2210).apply(lp);
        } else if (source instanceof ServiceOfferingSchema so) {
            externalId = so.getExternalId();
            converted2210 = validator.validated(this::convertRel3ServiceOffering2210).apply(so);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "LegalParticipantSchema and ServiceOffering are supported only"
            );
        }
        var mapOf2210 = converter2210.convert(converted2210);
        var withExternalId = new HashMap<>(mapOf2210.claims());
        withExternalId.put("externalId", externalId);
        return new Claims(withExternalId, mapOf2210.vocabularies());
    }

    private org.eclipse.tractusx.selfdescriptionfactory.model.v2210.LegalParticipantSchema convertRel3LegalParticipant2210(
            org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalParticipantSchema source
    ) {
        return new org.eclipse.tractusx.selfdescriptionfactory.model.v2210.LegalParticipantSchema()
                .type(source.getType())
                .holder(source.getHolder())
                .issuer(source.getIssuer())
                .bpn(source.getBpn())
                .registrationNumber(source.getRegistrationNumber().stream()
                        .map(rNum -> objectMapper.convertValue(rNum, org.eclipse.tractusx.selfdescriptionfactory.model.v2210.RegistrationNumberSchema.class))
                        .collect(Collectors.toSet()))
                .headquarterAddress(convertCountryCode(source.getHeadquarterAddressCountry()))
                .legalAddress(convertCountryCode(source.getLegalAddressCountry()));
    }

    private AddressSchema convertCountryCode(String countryCode) {
        return new AddressSchema().countryCode(countryCode);
    }

    private org.eclipse.tractusx.selfdescriptionfactory.model.v2210.ServiceOfferingSchema convertRel3ServiceOffering2210(
            org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema source
    ) {
        var aggregationOf = Utils.getNonEmptyListFromCommaSeparated(source.getAggregationOf(), Utils::uriFromStr).orElse(null);
        var termsAndConditions = Utils.getNonEmptyListFromCommaSeparated(source.getTermsAndConditions(), this::getTermsAndConditions).orElse(null);
        var policy = Utils.getNonEmptyListFromCommaSeparated(source.getPolicies(), Function.identity()).orElse(null);
        return new org.eclipse.tractusx.selfdescriptionfactory.model.v2210.ServiceOfferingSchema()
                .type(source.getType())
                .holder(source.getHolder())
                .issuer(source.getIssuer())
                .providedBy(source.getProvidedBy())
                .aggregationOf(aggregationOf)
                .termsAndConditions(termsAndConditions)
                .policy(policy)
                .dataAccountExport(List.of(
                        new DataAccountExportSchema()
                                .requestType(DataAccountExportSchema.RequestTypeEnum.EMAIL)
                                .accessType(DataAccountExportSchema.AccessTypeEnum.DIGITAL)
                                .formatType("json")
                        )
                );
    }
}
