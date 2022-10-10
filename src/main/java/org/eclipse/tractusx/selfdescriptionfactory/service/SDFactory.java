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
import foundation.identity.jsonld.JsonLDUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDFactory {

    // Namespace for the Self-Description document context

    @Value("${app.verifiableCredentials.schemaUrl}")
    private String schemaUrl;

    private URI sdVocURI;

    @Value("${app.verifiableCredentials.durationDays:90}")
    private int duration;

    private final CustodianWallet custodianWallet;

    @PostConstruct
    private void init() {
        try (InputStream sdVocIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/sd-document-v0.1.jsonld")) {
            assert sdVocIs != null;
            sdVocURI = URI.create(schemaUrl);
            VerifiableCredentialContexts.CONTEXTS.put(sdVocURI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, sdVocIs));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a VerifiableCredential on base of provided claims
     *
     * @param id        VC identity
     * @param claims    claims to be included to the VerifiableCredentials
     * @param holderBpn DID or BPN of the Holder for given claims
     * @param issuerBpn DID or BPN of the Issuer for the signature
     * @param documentType type of the document in the credentialSubject
     * @return VerifiableCredential signed by CatenaX authority
     */
    public VerifiableCredential createVC(String id, Map<String, Object> claims, Object holderBpn, Object issuerBpn, Object documentType) {
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
}
