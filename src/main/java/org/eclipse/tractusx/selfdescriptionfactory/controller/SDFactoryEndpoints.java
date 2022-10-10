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

package org.eclipse.tractusx.selfdescriptionfactory.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.dto.SDDocumentDto;
import org.eclipse.tractusx.selfdescriptionfactory.service.SDFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
public class SDFactoryEndpoints {

    private final SDFactory sdFactory;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {"application/vc+ld+json"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole(@securityRoles.createRole)")
    public VerifiableCredential createSelfDescription(@RequestBody MultiValueMap<String, Object> sdDocumentDto) {
        var map = sdDocumentDto.toSingleValueMap();
        var holder = map.remove("holder");
        var issuer = map.remove("issuer");
        if (holder == null || issuer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "holder and issuer should be defined in request");
        }
        var type = map.remove("type");
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field type (type of document) should be defined in request");
        }
        map.values().removeAll(Collections.singleton(null));
        return sdFactory.createVC(UUID.randomUUID().toString(), map, holder, issuer, type);
    }

    @PostMapping(value = "/old", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/vc+ld+json"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole(@securityRoles.createRole)")
    public VerifiableCredential createSelfDescriptionOld(@RequestBody SDDocumentDto dto) {
        var map = new LinkedMultiValueMap<String, Object>();
        map.add("company_number", dto.getCompany_number());
        map.add("headquarter_country", dto.getHeadquarter_country());
        map.add("legal_country", dto.getLegal_country());
        map.add("service_provider", dto.getService_provider());
        map.add("type", dto.getSd_type());
        map.add("bpn", dto.getBpn());
        map.add("holder", dto.getHolder());
        map.add("issuer", dto.getIssuer());
        return createSelfDescription(map);
    }
}
