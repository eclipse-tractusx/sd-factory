/********************************************************************************
 * Copyright (c) 2022,2024 T-Systems International GmbH
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.api.vrel3.ApiApiDelegate;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.AuthChecker;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SDFactory implements ApiApiDelegate, InitializingBean {
    @Value("${app.verifiableCredentials.durationDays:90}")
    private int duration;

    private final ConversionService conversionService;
    private final ClearingHouse clearingHouse;
    private final Environment environment;
    private final AuthChecker authChecker;

    private Function<SelfdescriptionPostRequest, ResponseEntity<Void>> decoratedFunction;

    @Override
    public ResponseEntity<Void> selfdescriptionPost(SelfdescriptionPostRequest selfdescriptionPostRequest) {
        return decoratedFunction.apply(selfdescriptionPostRequest);
    }

    private ResponseEntity<Void> doWork(SelfdescriptionPostRequest selfdescriptionPostRequest) {
        var processed = Objects.requireNonNull(conversionService.convert(selfdescriptionPostRequest, SelfDescription.class), "Converted SD-Document is null. Very strange");
        var verifiableCredential = VerifiableCredential.builder()
                .contexts(processed.getContexts())
                .id(URI.create("http://example.org/" + UUID.randomUUID()))
                .issuer(URI.create(processed.getIssuer()))
                .issuanceDate(new Date())
                .expirationDate(Date.from(Instant.now().plus(Duration.ofDays(duration))))
                .credentialSubject(CredentialSubject.fromJsonObject(processed))
                .type(processed.getType())
                .build();
        clearingHouse.sendToClearingHouse(verifiableCredential, processed.getExternalId());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public void afterPropertiesSet() {
        decoratedFunction = Arrays.asList(environment.getActiveProfiles()).contains("test")
                ? this::doWork
                : authChecker.getAuthorizedFn(this::doWork);
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class SelfDescription extends LinkedHashMap<String, Object> {
         private final List<URI> contexts;
         private final String holder;
         private final String issuer;
         private final String externalId;
         private final String type;
    }
}
