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

package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.service.keycloak.KeycloakManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public abstract class ClearingHouse {
    @Value("${app.usersDetails.clearingHouse.uri}")
    private String clearingHouseUrl;
    @Autowired
    protected KeycloakManager keycloakManager;

    public abstract void doWork(String url, VerifiableCredential payload, String externalId, String token);

    public void sendToClearingHouse(VerifiableCredential verifiableCredential, String externalId) {
        doWork(clearingHouseUrl, verifiableCredential, externalId, "Bearer ".concat(keycloakManager.getToken("clearingHouse")));
    }
}
