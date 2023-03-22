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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

@Slf4j
@Service
@RequestScope
public class CustodianWallet {
    @Value("${app.usersDetails.custodianWallet.uri}")
    private String uri;
    private final KeycloakManager keycloakManager;
    private final ObjectMapper objectMapper;
    private final Function<Throwable, ResponseStatusException> errMapper;
    private JsonNode walletInfo = null;

    public CustodianWallet(KeycloakManager keycloakManager, ObjectMapper objectMapper) {
        this.keycloakManager = keycloakManager;
        this.objectMapper = objectMapper;
        errMapper = e -> io.vavr.API.Match(e).of(
                Case($(instanceOf(WebClientResponseException.class)), err -> {
                    log.error("WebClientResponseException", err);
                    return Try.of(() -> objectMapper.readValue(err.getResponseBodyAsByteArray(), JsonNode.class).get("message").asText()
                    ).map(errString -> new ResponseStatusException(err.getStatusCode(), "Custodian Wallet problem: " + errString, err)
                    ).recover(mapperErr -> new ResponseStatusException(err.getStatusCode(), err.getMessage(), err)
                    ).get();
                }),
                Case($(instanceOf(WebClientRequestException.class)), err -> {
                    log.error("WebClientRequestException", err);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Custodian Wallet problem", err);
                }),
                Case($(), err -> {
                    log.error("unknown error", err);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error", err);
                })
        );
    }

    private String getToken() {
        return Try.of(() -> keycloakManager.getKeycloack("custodianWallet").tokenManager().getAccessTokenString())
                .getOrElseThrow(err -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error when get Access Token for the Custodian Wallet",
                        err
                ));
    }

    public VerifiableCredential getSignedVC(VerifiableCredential objToSign) {
        return Try.of(() -> WebClient.create(uri).post()
                        .uri(uriBuilder -> uriBuilder.pathSegment("credentials").build())
                        .header("Authorization", "Bearer ".concat(getToken()))
                        .bodyValue(objToSign)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(VerifiableCredential.class)
                        .block()
                ).recoverWith(Utils.mapFailure(errMapper))
                .onFailure(err -> Try.of(() -> objectMapper.writeValueAsString(objToSign)).onSuccess(json -> log.error("Error in custodian. Original JSON is: {}", json)))
                .get();
    }

    public JsonNode getWalletData(String bpnNumber) {
        if (Objects.isNull(walletInfo)) {
            walletInfo = Try.of(() -> WebClient.create(uri).get()
                            .uri(uriBuilder -> uriBuilder.pathSegment("wallets", bpnNumber).build())
                            .header("Authorization", "Bearer ".concat(getToken()))
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(JsonNode.class)
                            .block()
                    ).recoverWith(Utils.mapFailure(errMapper))
                    .get();
        }
        return walletInfo;
    }

}