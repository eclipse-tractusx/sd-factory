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

package org.eclipse.tractusx.selfdescriptionfactory;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialKeywords;
import foundation.identity.jsonld.JsonLDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.api.tagus.ApiApiDelegate;
import org.eclipse.tractusx.selfdescriptionfactory.model.tagus.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.AuthChecker;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SDFactory implements ApiApiDelegate, InitializingBean {

    private final ConversionService conversionService;
    private final ClearingHouse clearingHouse;
    private final Environment environment;
    private final AuthChecker authChecker;

    private Function<SelfdescriptionPostRequest, ResponseEntity<Void>> decoratedFunction;

    @Override
    public ResponseEntity<Void> selfdescriptionPost(SelfdescriptionPostRequest selfdescriptionPostRequest) {
        return decoratedFunction.apply(selfdescriptionPostRequest);
    }

    private ResponseEntity<Void> doWork(org.eclipse.tractusx.selfdescriptionfactory.model.tagus.SelfdescriptionPostRequest selfdescriptionPostRequest) {
        var selfDescription = Objects.requireNonNull(conversionService.convert(selfdescriptionPostRequest, SelfDescription.class), "Converted SD-Document is null. Very strange");
        var presentation = VerifiablePresentation.builder().build();
        JsonLDUtils.jsonLdAddAsJsonArray(presentation, VerifiableCredentialKeywords.JSONLD_TERM_VERIFIABLECREDENTIAL, selfDescription.getVerifiableCredentialList());
        clearingHouse.sendToClearingHouse(presentation, selfDescription.getExternalId());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public void afterPropertiesSet() {
        decoratedFunction = Arrays.asList(environment.getActiveProfiles()).contains("test")
                ? this::doWork
                : authChecker.getAuthorizedFn(this::doWork);
    }
}
