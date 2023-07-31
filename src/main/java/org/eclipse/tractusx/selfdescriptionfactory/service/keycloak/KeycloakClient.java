
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


import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.net.URI;
import java.util.Map;

public interface KeycloakClient {
    @RequestLine("POST /realms/{realm}/protocol/openid-connect/token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("grant_type=client_credentials&client_id={client_id}&client_secret={client_secret}&scope=openid")
    Map<String, Object> getTokens(URI serverUrl, @Param("realm") String realm, @Param("client_id") String clientId, @Param("client_secret") String clientSecret);
}
