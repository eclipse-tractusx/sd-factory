/********************************************************************************
 * Copyright (c) 2023 T-Systems International GmbH
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.ws.rs.NotAuthorizedException;
import java.util.Optional;

@Component("CustodianWalletHealthIndicator")
@Slf4j
public class CustodianWalletHealthIndicator implements HealthIndicator {

    @Value("${app.usersDetails.custodianWallet.uri}")
    private String url;

    @Autowired
    private KeycloakManager keycloakManager;

    @Override
    public Health health() {
        try {
            String token = keycloakManager.getKeycloack("custodianWallet").tokenManager().getAccessTokenString();
            if (token != null) {
                WebClient.ResponseSpec responseSpec = WebClient.create(url).get()
                        .uri(uriBuilder -> uriBuilder.pathSegment("wallets").build())
                        .header("Authorization", "Bearer ".concat(token))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve();
                HttpStatusCode httpCode = Optional.of(responseSpec.toBodilessEntity().block().getStatusCode()).get();
                if (httpCode.value() == HttpStatus.OK.value()) {
                    return Health.up()
                            .withDetail("Access", "Custodian wallet is accessible from SD factory")
                            .build();
                }
            }
            return Health.down()
                    .withDetail("error", "Custodian wallet is not accessible from SD factory")
                    .build();
        } catch(WebClientResponseException |
                WebClientRequestException |
                NotAuthorizedException requestException) {
            return Health.down()
                    .withDetail("error", "Custodian wallet is not accessible from SD factory")
                    .build();
        }

    }
}
