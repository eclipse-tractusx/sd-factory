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

package org.eclipse.tractusx.selfdescriptionfactory.service;

import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import foundation.identity.jsonld.JsonLDUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.api.SelfdescriptionApiDelegate;
import org.eclipse.tractusx.selfdescriptionfactory.model.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDFactory implements SelfdescriptionApiDelegate {
    @Value("${app.verifiableCredentials.schemaUrl}")
    private String schemaUrl;

    private URI sdVocURI;

    @Value("${app.verifiableCredentials.durationDays:90}")
    private int duration;

    private final CustodianWallet custodianWallet;

    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        try {
            objectMapper = JsonMapper.builder()
                    .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
                    .build();
            sdVocURI = URI.create(schemaUrl);
            URLConnection connection = sdVocURI.toURL().openConnection();
            String redirect = connection.getHeaderField("Location");
            if (redirect != null){
                connection = new URL(redirect).openConnection();
            }
            try (InputStream sdVocIs = connection.getInputStream()) {
                assert sdVocIs != null;
                var sdVoc = sdVocIs.readAllBytes();
                var sdVocS = new String(sdVoc);
                VerifiableCredentialContexts.CONTEXTS.put(sdVocURI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, new ByteArrayInputStream(sdVoc)));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private VerifiableCredential createVC(Map<String, Object> claims, Object holderBpn, Object issuerBpn, Object documentType) {
        var credentialSubject = CredentialSubject.builder()
                .claims(claims)
                .build();
        var verifiableCredential = VerifiableCredential.builder()
                .context(sdVocURI)
                .issuanceDate(new Date())
                .expirationDate(Date.from(Instant.now().plus(Duration.ofDays(duration))))
                .credentialSubject(credentialSubject)
                .build();
        JsonLDUtils.jsonLdAdd(verifiableCredential, "issuerIdentifier", issuerBpn);
        JsonLDUtils.jsonLdAdd(verifiableCredential, "holderIdentifier", holderBpn);
        JsonLDUtils.jsonLdAdd(verifiableCredential, "type", documentType);

        return custodianWallet.getSignedVC(verifiableCredential);
    }


    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole(@securityRoles.createRole)")
    public ResponseEntity<Map<String, Object>> selfdescriptionPost(SelfdescriptionPostRequest selfdescriptionPostRequest) {
        Map<String, Object> map = objectMapper.convertValue(selfdescriptionPostRequest, Map.class);
        var holder = map.remove("holder");
        var issuer = map.remove("issuer");
        var type = map.get("type");
        map.values().removeAll(Collections.singleton(null));
        var res = createVC(map, holder, issuer, type);
        return new ResponseEntity<>(res.toMap(), HttpStatus.CREATED);
    }

}
