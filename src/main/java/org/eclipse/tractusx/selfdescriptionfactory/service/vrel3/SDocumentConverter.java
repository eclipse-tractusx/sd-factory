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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.API;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalPersonSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.AddressSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.DataAccountExportSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.TermsAndConditionsSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Component
@RequiredArgsConstructor
public class SDocumentConverter implements Converter<SelfdescriptionPostRequest, Claims> {
    private final ObjectMapper objectMapper;
    private final org.eclipse.tractusx.selfdescriptionfactory.service.v2210.SDocumentConverter converter2210;
    private final org.eclipse.tractusx.selfdescriptionfactory.service.Validator validator;

    @Value("${app.maxRedirect:5}")
    private int maxRedirect;
    @Value("${app.verifiableCredentials.schemaRel3Url}")
    private String schemaRel3;

    @Override
    public Claims convert(@NonNull SelfdescriptionPostRequest source) {
        return API.Match(source).of(
                Case($(instanceOf(LegalPersonSchema.class)), s -> Function.<LegalPersonSchema>identity()
                        .andThen(validator.validated(this::convertRel3LegalPerson2210))
                        .andThen(converter2210::convert)
                        .apply(s)),
                Case($(instanceOf(ServiceOfferingSchema.class)), s -> Function.<ServiceOfferingSchema>identity()
                    .andThen(validator.validated(this::convertRel3ServiceOffering2210))
                    .andThen(converter2210::convert)
                    .apply(s)),
                Case($(), s -> new Claims(objectMapper.convertValue(s, new TypeReference<>() {}), URI.create(schemaRel3)))
        );
    }

    private org.eclipse.tractusx.selfdescriptionfactory.model.v2210.LegalPersonSchema convertRel3LegalPerson2210(
            org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalPersonSchema source
    ) {
        return new org.eclipse.tractusx.selfdescriptionfactory.model.v2210.LegalPersonSchema()
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
        return new org.eclipse.tractusx.selfdescriptionfactory.model.v2210.ServiceOfferingSchema()
                .type(source.getType())
                .holder(source.getHolder())
                .issuer(source.getIssuer())
                .providedBy(source.getProvidedBy())
                .aggregationOf(Optional.ofNullable(source.getAggregationOf())
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream).map(String::trim)
                        .map(Utils::uriFromStr).collect(Collectors.toList()))
                .termsAndConditions(Optional.ofNullable(source.getTermsAndConditions())
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream).map(String::trim)
                        .map(this::getTermsAndConditions).collect(Collectors.toList()))
                .policy(Optional.ofNullable(source.getPolicies())
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream).map(String::trim)
                        .collect(Collectors.toList()))
                .dataAccountExport(List.of(
                        new DataAccountExportSchema()
                                .requestType(DataAccountExportSchema.RequestTypeEnum.EMAIL)
                                .accessType(DataAccountExportSchema.AccessTypeEnum.DIGITAL)
                                .formatType("json")
                        )
                );
    }
    private TermsAndConditionsSchema getTermsAndConditions(String urlStr) {
        return Try.of(() -> new URL(urlStr))
                .mapTry(url -> Utils.getConnectionIfRedirected(url, maxRedirect))
                .flatMap(urlConnection -> Try.withResources(urlConnection::getInputStream).of(DigestUtils::sha256Hex))
                .map(sha-> new TermsAndConditionsSchema()
                        .URL(URI.create(urlStr))
                        .hash(sha))
                .recoverWith(Utils.mapFailure(err ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Could not retrieve TermsAndConditions from '"+ urlStr +"'",
                                err)
                        )
                ).get();
    }
}
