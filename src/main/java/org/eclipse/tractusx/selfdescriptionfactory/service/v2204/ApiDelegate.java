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

package org.eclipse.tractusx.selfdescriptionfactory.service.v2204;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.api.v2204.ApiApiDelegate;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2204.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.SDFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiDelegate implements ApiApiDelegate {
    private final SDFactory sdFactory;

    public ResponseEntity<Map<String, Object>> selfdescriptionPost(SelfdescriptionPostRequest selfdescriptionPostRequest) {
        return sdFactory.createVC(selfdescriptionPostRequest);
    }
}
