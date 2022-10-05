/********************************************************************************
 * Copyright (c) 2022 BMW GmbH
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package net.catenax.selfdescriptionfactory;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import foundation.identity.jsonld.JsonLDObject;
import net.catenax.selfdescriptionfactory.service.SDFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VCTest {

    @Autowired
    private SDFactory sdFactory;

    @Test
    public void testVc() {
        VerifiableCredential verifiableCredential = createVc();
        Assert.assertNotNull(verifiableCredential);
        String representation = verifiableCredential.toJson(true);
        Assert.assertFalse(representation.isBlank());
        System.out.println(representation);
    }

    private VerifiableCredential createVc() {
        var uuid = UUID.randomUUID();
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("company_number", "DE-123");
        claims.put("headquarter_country", "DE");
        claims.put("legal_country", "DE");
        claims.put("bpn", "12345678");
        return sdFactory.createVC(uuid.toString(), claims, "holder", "issuer", "");
    }

    @Test
    public void testJsonLd() {
        JsonLDObject jsonLDObject = JsonLDObject.fromJson("""
                {
                    "@context" : {
                        "company_number": "https://schema.org/taxID",
                        "headquarter.country": "https://schema.org/addressCountry",
                        "legal.country": "https://schema.org/addressCountry"
                     },
                     "company_number": " DE123",
                     "headquarter.country": "DE",
                     "legal.country": "DE"
                }
                """);
        System.out.println(jsonLDObject.toJson(true));
    }
}