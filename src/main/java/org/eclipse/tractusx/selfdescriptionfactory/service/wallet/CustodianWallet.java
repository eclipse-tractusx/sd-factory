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

package org.eclipse.tractusx.selfdescriptionfactory.service.wallet;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustodianWallet {

    @Value("${app.custodianWallet.uri}")
    private String uri;

    private final TokenManager tokenManager;
    private final ObjectMapper objectMapper;

    public VerifiableCredential getSignedVC(VerifiableCredential objToSign) {
        try {
            var payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objToSign);
            System.err.println(payload);
            return WebClient.create(uri).post()
                    .uri(uriBuilder -> uriBuilder.pathSegment("credentials").build())
                    .header("Authorization", "Bearer ".concat(tokenManager.getAccessTokenString()))
                    .bodyValue(objToSign)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(VerifiableCredential.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error(e.getResponseBodyAsString());
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}