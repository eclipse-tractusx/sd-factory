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

package net.catenax.selfdescriptionfactory.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Objects;
import java.util.Optional;

@Configuration
public class KeycloakConfig {

    @Value("${app.custodianWallet.auth-server-url}")
    private String serverUrl;
    @Value("${app.custodianWallet.realm}")
    private String realm;
    @Value("${app.custodianWallet.username:#{null}}")
    private String username;
    @Value("${app.custodianWallet.password:#{null}}")
    private String password;
    @Value("${app.custodianWallet.clientId}")
    private String clientId;
    @Value("${app.custodianWallet.clientSecret}")
    private String clientSecret;


    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @ApplicationScope
    public TokenManager tokenManager() {
        var keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build());
        if (Objects.isNull(username)) {
            keycloak.grantType("client_credentials");
        }
        return keycloak.build().tokenManager();
    }

}
