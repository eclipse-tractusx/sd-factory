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

package org.eclipse.tractusx.selfdescriptionfactory.service.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.selfdescriptionfactory.config.TechnicalUsersDetails;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KeycloakManager {

    private static final long REFRESH_GAP = 60L;
    private final TechnicalUsersDetails technicalUsersDetails;
    private final Map<String, Map<String, Object>> tokenMap = new HashMap<>();

    private final KeycloakClient keycloakClient;

    private final ObjectMapper mapper;

    @SneakyThrows
    public String getToken(String name) {
        var kk = tokenMap.get(name);
        var details = technicalUsersDetails.getUsersDetails().get(name);
        if (Objects.isNull(details))
            return null;
        if (!Objects.isNull(kk)) {
            var token = kk.get("access_token").toString();
            var jwt = mapper.readValue(Base64.getDecoder().decode(token.split("\\.")[1]), new TypeReference<Map<String, Object>>(){});
            var expirationInstant = Instant.ofEpochSecond((Integer) jwt.get("exp"));
            if (Duration.between(Instant.now(), expirationInstant).compareTo(Duration.ofSeconds(REFRESH_GAP)) > 0) {
                //token still valid
                return token;
            } else {
                //try to obtain access token using refresh token
                var refreshToken = kk.get("refresh_token");
                if (!Objects.isNull(refreshToken)) {
                    var refreshedToken = refresh(refreshToken.toString(), name, details);
                    if (refreshedToken != null) return refreshedToken;
                }
            }
        }
        // Trying to get token using supplied credentials
        var param = new HashMap<String, String>();
        if (details.username() == null || details.username().isBlank()) {
            param.put("grant_type", "client_credentials");
        } else {
            param.put("grant_type", "password");
            param.put("username", details.username());
            param.put("password", details.password());
        }
        param.put("client_id", details.clientId());
        param.put("client_secret", details.clientSecret());
        param.put("scope", "openid");
        kk = keycloakClient.getTokens(URI.create(details.serverUrl()), details.realm(), param);
        tokenMap.put(name, kk);
        return kk.get("access_token").toString();
    }

    private String refresh(String refreshToken, String name, TechnicalUsersDetails.UserDetail details) {
        try {
            var token = mapper.readValue(Base64.getDecoder().decode(refreshToken.split("\\.")[1]), new TypeReference<Map<String, Object>>() {});
            var expirationInstant = Instant.ofEpochSecond((Integer) token.get("exp"));
            if (Duration.between(Instant.now(), expirationInstant).compareTo(Duration.ofSeconds(REFRESH_GAP)) > 0) {
                var kk = keycloakClient.getTokens(
                        URI.create(details.serverUrl()), details.realm(), Map.of(
                                "grant_type", "refresh_token",
                                "client_id", details.clientId(),
                                "client_secret", details.clientSecret(),
                                "scope", "open_id",
                                "refresh_token", refreshToken
                        )
                );
                tokenMap.put(name, kk);
                return kk.get("access_token").toString();
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }
}
