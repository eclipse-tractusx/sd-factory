/********************************************************************************
 * Copyright (c) 2022, 2025 T-Systems International GmbH
 * Copyright (c) 2022, 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialKeywords;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import foundation.identity.jsonld.JsonLDUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.eclipse.tractusx.selfdescriptionfactory.model.tagus.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.ValidityChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class SelfDescriptionFactoryApplicationTests {

    @Autowired
    ConversionService conversionService;
    @Autowired
    ValidityChecker validityChecker;
    ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    static final String serviceOfferingStr =
            """
                    {
                      "externalId": "ID01234-123-4321",
                      "type": "ServiceOffering",
                      "holder": "BPNL000000000000",
                      "termsAndConditions": {
                        "gx:URL": "https://shop.t-systems.de/eshop/medias/otc-GTC-TSI.pdf?context=bWFzdGVyfGVtYWlsLWF0dGFjaG1lbnRzfDEwNTYyMHxhcHBsaWNhdGlvbi9wZGZ8ZW1haWwtYXR0YWNobWVudHMvaDZmL2g5YS9oMDAvODg2MzA1NDgyMzQ1NC5wZGZ8ZGE5Y2UzNjRlM2Q0OWVhZDRlNjk2YWRlZDI3ZGYxYTk5YTlmM2NmMDIwODUwYWM3NWY0MzQ5OTI0YTU2YTA4ZA",
                        "gx:hash": "A884DECDE0E00D75FA204A78E0A000"
                      },
                      "providedBy":"https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/f3f01f41-e1fe-4c7e-90c7-14b06a1b4b72/1988d094-5c57-4fa6-8fd7-837a68991a3e.json",
                      "policy": "https://shop.t-systems.de/eshop/medias/otc-service-specification-TSI.pdf?context=bWFzdGVyfGVtYWlsLWF0dGFjaG1lbnRzfDI5NTMzNzd8YXBwbGljYXRpb24vcGRmfGVtYWlsLWF0dGFjaG1lbnRzL2g0My9oMDMvaDAwLzg4NjY0MzU0OTgwMTQucGRmfGJiMjUyMmI3OGFjNWYwYzc4MDUxOWNhOTdjOWRmOTNhNmJmNGEzYTU4YTc1YTAxOTZjNzFhZGY5M2Y0YjUwZWY",
                      "dataProtectionRegime": [
                         "GDPR2016"
                       ],
                      "dataAccountExport": {
                         "gx:requestType": "API",
                         "gx:accessType": "digital",
                         "gx:formatType": "application/json"
                      },
                      "connectorUrl":"http://company.connector-url.org/",
                      "attachment": [
                        {
                          "@context": [
                            "https://www.w3.org/2018/credentials/v1",
                            "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "https://w3id.org/security/suites/jws-2020/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/98bc7605-0b5e-41f3-8c09-216cd8d614c4.json",
                          "credentialSubject": {
                            "@context": {
                              "ctxsd": "https://w3id.org/catena-x/core#"
                            },
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:LegalParticipant",
                            "ctxsd:bpn": "BPNL000000000000",
                            "gx:legalName": "T-Systems International GmbH",
                            "gx:headquarterAddress": {
                              "gx:countrySubdivisionCode": "DE-BY"
                            },
                            "gx:legalAddress": {
                              "gx:countrySubdivisionCode": "DE-NW"
                            },
                            "gx:legalRegistrationNumber": {
                              "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/35978ca0-4336-48d3-acdc-8f7a097d938c.json"
                            },
                            "issuer": "did:web:carla.dih-cloud.com:dev:signer_service",
                            "issuanceDate": "2023-12-14T15:04:57.480Z",
                            "proof": {
                              "type": "JsonWebSignature2020",
                              "created": "2024-01-01T02:42:35.581Z",
                              "proofPurpose": "assertionMethod",
                              "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..P9uwzVQ86qf8o0oD7kxbcC0isaPGfdGfQT4B5LTnoI2emX4Uq46dS_S9BjvH7mDRX9Zg2ckTclvhR5shCPOAOaSPqEcpwcOGr3zIwrASL-onoWVHULVClMjzSLgkzgpv-hfleJQ3fGsMYVKHxz6E8Pp9-ANWwKwupXJN5nOeQwhaFIQyg69BJE93qtV7AjZUE3AiRpIwzYYHC0vo_u8ThWnBENy148bgpqF4cUs2C0Od89FrQIjYky7tAED27rFZ51IfpBhaJo9-lf3XTRWkKPPxklFfdzHnl9xLtDWH1JyusSQFS2wiJHJNrKRDaT3uwubsLCgx06cdORrqyJ8NeMm1ijllW6baPxEQG2VUVfotVj6EZZg_z_9aDgN7n9w4IiMQXYLJR1TnqPq-iNlj8IVs4l84ZJFvrmykCwOGbVaKYsOMFs276ydgfUlzY01HxYAIxZ9LEArbhVbRT88quBuirPou1E0K2E3FbZyV8Loh9aNrJxssrLJ8iTFIWsSdbV20IN2EJsrBAPFUP7i0ZyFO1L4rWLFCjiN3jSyC9kMPUigpEEt2Eu-p9e27n6SmC1FXmSkvHdeSNJyIeHPgfPEG4JDcvd6v_89wqBb2HJPx1GnpQqOWM-Ax3xtr_AUITHsvBa1JyWu4DUONmhtuIUJsE-FBQG_YO7P-sjmP2l8",
                              "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1"
                            }
                          }
                        },
                        {
                          "@context": [
                            "https://www.w3.org/2018/credentials/v1",
                            "https://w3id.org/security/suites/jws-2020/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/35978ca0-4336-48d3-acdc-8f7a097d938c.json",
                          "issuer": "did:web:gx-notary.gxdch.dih.telekom.com:v1",
                          "issuanceDate": "2024-01-01T02:42:34.617Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:legalRegistrationNumber",
                            "gx:vatID": "DE118645675",
                            "gx:vatID-countryCode": "DE"
                          },
                          "evidence": [
                            {
                              "gx:evidenceURL": "https://ec.europa.eu/taxation_customs/vies/services/checkVatService",
                              "gx:executionDate": "2024-01-01T02:42:34.617Z",
                              "gx:evidenceOf": "gx:vatID"
                            }
                          ],
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2024-01-01T02:42:34.635Z",
                            "proofPurpose": "assertionMethod",
                            "verificationMethod": "did:web:gx-notary.gxdch.dih.telekom.com:v1#X509-JWK2020",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..eWQFtqfP6vnA2hwo8wHMDD5w6ZkAs62wTGpBHMhi6ZFWijeZcQbQbtFerZEk5j2UDvfedSNLjFUHfhDMBNt9OA-S7QyH7uCT9Td6jNIBrJqar-bTAiABqvUNfebLs8eRtAtGr0y_vmuxNd0EUPOAINQrK85q8GxyUa3X4EGd9b7jchKW2oGl-rgsHq7OxNXvSd8OxY330rsCvmKmqyPy6ga-MDQwBZYXRDjTWMQ7Df8vmMTy8ACZMOJq_ajjjHTHxi7l_H3yD0EacfUcwDSFmH5SFjiHMiw88MBkJAAqLBj0ZlWzDClzbJqZGVLFLW-2KNRV-Eix5wu_21KIHCFl4TWouXD2kuY24ARx6HzFiklgOqhuL0x6NVTDI2pB6Z8CiQbNAusPxPAelczU56PQW6sIwo4s6fvMZWQLVTVtVDu5Z02GVYAVcBNUs9nKHAK9p4lu2PcIWg5LMOAgDYCx-tUW78XhP20YhboReNByORb15zlcGkVo-JVB0PtpdsEEBLnGg09Of6-z6moq20hcKOO0-ilrrwf3NjYB32QWKFt8XRAuxp9SayvTUVUr2nFSBoimgAr7tPkL9w-_DdOTY_9pwv5YtqnEE3ii22J7tls5eWKhaxs7hXatZqY20rWna0ccOGuEbTkHip4J-yMhy4mIJ2dzyxZHnqN8LAtWeTo"
                          }
                        },
                        {
                          "@context": [
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/2018/credentials/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/d56122d4-679e-4290-a36d-09405df912cb.json",
                          "issuer": "did:web:carla.dih-cloud.com:dev:signer_service",
                          "issuanceDate": "2024-02-01T20:16:19Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:GaiaXTermsAndConditions",
                            "gx:termsAndConditions": "The PARTICIPANT signing the Self-Description agrees as follows:\\n- to update its descriptions about any changes, be it technical, organizational, or legal - especially but not limited to contractual in regards to the indicated attributes present in the descriptions.\\n\\nThe keypair used to sign Verifiable Credentials will be revoked where Gaia-X Association becomes aware of any inaccurate statements in regards to the claims which result in a non-compliance with the Trust Framework and policy rules defined in the Policy Rules and Labelling Document (PRLD).\\n"
                          },
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2024-01-01T04:21:12.913Z",
                            "proofPurpose": "assertionMethod",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..aYEdMRMLH1E7IfcA9mccspv_oiGrrkV7KZRQWcCSHM2a2Q4omGyTy9FkHWEZ8bA87vLgcJUsaOyA1ggYDDT0OQEtXiKeEZaYkMCc5Av0iQzyiB_1Ow2g7DXzGZ4kRoFQXbhFWPLn-f3f4ihkuIdF8MO9Iijzwbju2Jon4AxN5UMEcq1vGNkAn1PsW6H6myzU9wlk287GKpMKnIcL_brJ6IbbZq7gz7tZMpkf_epMaRTftkcPg3tUhPQRgtNOJ_N1FGvSByghVLDNQGuF4nVB3ZWvab87h_DcsGZwKg9farED0AiMWuzEFV-Ft99mkwN1rslv-rm7RXQcKuuHlTVYARGFbUaV4Tb4joYr2F9IVjWNq9V4xxWt988T7ROPWAuYsy9VS9VeVTI4kvqEu-VSamHDW34Rl6tX1bE-5c0OA2Hlq41QQXsMhjLZh0vW07-jENqaykVe9IBNS8o5Sfc4WJYukwa0axQ3DGl5AlpQMF_uJwG1hJOUyaO8fifC4xNDAh_DRSpo2Wfsk1dD3HVw5IH1YKGzDRrzXz1sxRW-SSiSXrcibxr63V0hHFi2Rm96L5CvF2jD__CkmKWNGOK8d2ksQZkGNbWBcTx5hitTkAdn8dEj0lG5O178-demyC0XWHMR18e0MgMxtq0IutRO3OqNjfA-poIARrwK14NgcbM",
                            "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1"
                          }
                        }
                      ]
                    }
                    """;

    static Stream<Arguments> serviceOfferingProvider() {
        return Stream.of(
                Arguments.of(serviceOfferingStr, "Correct Service Offering")
        );
    }

    @ParameterizedTest(name = "{index} - {1}")
    @MethodSource("serviceOfferingProvider")
    public void testServiceOfferingConverter(String selfdescriptionRequestStr, String type) throws JsonProcessingException {
        var selfdescriptionPostRequest = validityChecker.getValidated(() -> objectMapper.readValue(selfdescriptionRequestStr, SelfdescriptionPostRequest.class)).get();
        var converted = conversionService.convert(selfdescriptionPostRequest, SelfDescription.class);
        assertNotNull(converted, "converted object should not be null");
        assertFalse(converted.getVerifiableCredentialList().isEmpty());
        System.out.println(objectMapper.writeValueAsString(converted.getVerifiableCredentialList()));
    }

    @Test
    public void testServiceOfferingConverterVp() throws JsonProcessingException {
        var selfdescriptionPostRequest = validityChecker.getValidated(() -> objectMapper.readValue(serviceOfferingStr, SelfdescriptionPostRequest.class)).get();
        var converted = conversionService.convert(selfdescriptionPostRequest, SelfDescription.class);
        assertNotNull(converted, "converted object should not be null");
        assertFalse(converted.getVerifiableCredentialList().isEmpty());
        System.out.println(objectMapper.writeValueAsString(converted.getVerifiableCredentialList()));

        var vp = VerifiablePresentation.builder()
                .id(URI.create("https://vp.id/".concat(UUID.randomUUID().toString())))
                .build();
        JsonLDUtils.jsonLdAddAsJsonArray(vp, VerifiableCredentialKeywords.JSONLD_TERM_VERIFIABLECREDENTIAL, converted.getVerifiableCredentialList());
        System.out.println(objectMapper.writeValueAsString(vp));

    }

    static final String serviceOfferingWrongSchemaStr =
            """
                    {
                      "externalId": "ID01234-123-4321",
                      "type": "ServiceOffering",
                      "holder": "BPNL000000000000",
                      "termsAndConditions": {
                        "gx:URL": "https://shop.t-systems.de/eshop/medias/otc-GTC-TSI.pdf?context=bWFzdGVyfGVtYWlsLWF0dGFjaG1lbnRzfDEwNTYyMHxhcHBsaWNhdGlvbi9wZGZ8ZW1haWwtYXR0YWNobWVudHMvaDZmL2g5YS9oMDAvODg2MzA1NDgyMzQ1NC5wZGZ8ZGE5Y2UzNjRlM2Q0OWVhZDRlNjk2YWRlZDI3ZGYxYTk5YTlmM2NmMDIwODUwYWM3NWY0MzQ5OTI0YTU2YTA4ZA",
                        "gx:hash": "A884DECDE0E00D75FA204A78E0A000"
                      },
                      "providedBy":"https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/f3f01f41-e1fe-4c7e-90c7-14b06a1b4b72/1988d094-5c57-4fa6-8fd7-837a68991a3e.json",
                      "policy": "https://shop.t-systems.de/eshop/medias/otc-service-specification-TSI.pdf?context=bWFzdGVyfGVtYWlsLWF0dGFjaG1lbnRzfDI5NTMzNzd8YXBwbGljYXRpb24vcGRmfGVtYWlsLWF0dGFjaG1lbnRzL2g0My9oMDMvaDAwLzg4NjY0MzU0OTgwMTQucGRmfGJiMjUyMmI3OGFjNWYwYzc4MDUxOWNhOTdjOWRmOTNhNmJmNGEzYTU4YTc1YTAxOTZjNzFhZGY5M2Y0YjUwZWY",
                      "dataProtectionRegime": [
                        "GDPR2016"
                      ],
                      "dataAccountExport": {
                        "gx:requestType": "API",
                        "gx:accessType": "digital",
                        "gx:formatType": "application/json"
                      },
                      "connectorUrl": "https://company.connector-url.org/",
                      "attachment": [
                      ]
                    }
                    """;

    @Test
    public void testServiceOfferingError() throws JsonProcessingException {
        var selfdescriptionPostRequest = objectMapper.readValue(serviceOfferingWrongSchemaStr, SelfdescriptionPostRequest.class);
        Assertions.assertThrows(ConstraintViolationException.class, () -> validityChecker.validate(selfdescriptionPostRequest));
    }

    static final String serviceOfferingWrongParticipantStr =
            """
                    {
                      "externalId": "ID01234-123-4321",
                      "type": "ServiceOffering",
                      "holder": "BPNL000000000001",
                       "providedBy":"https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/f3f01f41-e1fe-4c7e-90c7-14b06a1b4b72/1988d094-5c57-4fa6-8fd7-837a68991a3e.json",
                      "termsAndConditions": {
                       "gx:URL": "https://shop.t-systems.de/eshop/medias/otc-GTC-TSI.pdf?context=bWFzdGVyfGVtYWlsLWF0dGFjaG1lbnRzfDEwNTYyMHxhcHBsaWNhdGlvbi9wZGZ8ZW1haWwtYXR0YWNobWVudHMvaDZmL2g5YS9oMDAvODg2MzA1NDgyMzQ1NC5wZGZ8ZGE5Y2UzNjRlM2Q0OWVhZDRlNjk2YWRlZDI3ZGYxYTk5YTlmM2NmMDIwODUwYWM3NWY0MzQ5OTI0YTU2YTA4ZA",
                       "gx:hash": "A884DECDE0E00D75FA204A78E0A000"
                      },
                       "policy": "https://shop.t-systems.de/eshop/medias/otc-service-specification-TSI.pdf?context=bWFzdGVyfGVtYWlsLWF0dGFjaG1lbnRzfDI5NTMzNzd8YXBwbGljYXRpb24vcGRmfGVtYWlsLWF0dGFjaG1lbnRzL2g0My9oMDMvaDAwLzg4NjY0MzU0OTgwMTQucGRmfGJiMjUyMmI3OGFjNWYwYzc4MDUxOWNhOTdjOWRmOTNhNmJmNGEzYTU4YTc1YTAxOTZjNzFhZGY5M2Y0YjUwZWY",
                      "dataProtectionRegime": [
                        "GDPR2016"
                      ],
                      "dataAccountExport": {
                        "gx:requestType": "API",
                        "gx:accessType": "digital",
                        "gx:formatType": "application/json"
                      },
                      "connectorUrl": "https://company.connector-url.org/",
                      "attachment": [
                        {
                          "@context": [
                            "https://www.w3.org/2018/credentials/v1",
                            "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "https://w3id.org/security/suites/jws-2020/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/98bc7605-0b5e-41f3-8c09-216cd8d614c4.json",
                          "credentialSubject": {
                            "@context": {
                              "ctxsd": "https://w3id.org/catena-x/core#"
                            },
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:LegalParticipant",
                            "ctxsd:bpn": "BPNL000000000000",
                            "gx:legalName": "T-Systems International GmbH",
                            "gx:headquarterAddress": {
                              "gx:countrySubdivisionCode": "DE-BY"
                            },
                            "gx:legalAddress": {
                              "gx:countrySubdivisionCode": "DE-NW"
                            },
                            "gx:legalRegistrationNumber": {
                              "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/35978ca0-4336-48d3-acdc-8f7a097d938c.json"
                            },
                            "issuer": "did:web:carla.dih-cloud.com:dev:signer_service",
                            "issuanceDate": "2023-12-14T15:04:57.480Z",
                            "proof": {
                              "type": "JsonWebSignature2020",
                              "created": "2024-01-01T02:42:35.581Z",
                              "proofPurpose": "assertionMethod",
                              "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..P9uwzVQ86qf8o0oD7kxbcC0isaPGfdGfQT4B5LTnoI2emX4Uq46dS_S9BjvH7mDRX9Zg2ckTclvhR5shCPOAOaSPqEcpwcOGr3zIwrASL-onoWVHULVClMjzSLgkzgpv-hfleJQ3fGsMYVKHxz6E8Pp9-ANWwKwupXJN5nOeQwhaFIQyg69BJE93qtV7AjZUE3AiRpIwzYYHC0vo_u8ThWnBENy148bgpqF4cUs2C0Od89FrQIjYky7tAED27rFZ51IfpBhaJo9-lf3XTRWkKPPxklFfdzHnl9xLtDWH1JyusSQFS2wiJHJNrKRDaT3uwubsLCgx06cdORrqyJ8NeMm1ijllW6baPxEQG2VUVfotVj6EZZg_z_9aDgN7n9w4IiMQXYLJR1TnqPq-iNlj8IVs4l84ZJFvrmykCwOGbVaKYsOMFs276ydgfUlzY01HxYAIxZ9LEArbhVbRT88quBuirPou1E0K2E3FbZyV8Loh9aNrJxssrLJ8iTFIWsSdbV20IN2EJsrBAPFUP7i0ZyFO1L4rWLFCjiN3jSyC9kMPUigpEEt2Eu-p9e27n6SmC1FXmSkvHdeSNJyIeHPgfPEG4JDcvd6v_89wqBb2HJPx1GnpQqOWM-Ax3xtr_AUITHsvBa1JyWu4DUONmhtuIUJsE-FBQG_YO7P-sjmP2l8",
                              "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1"
                            }
                          }
                        },
                        {
                          "@context": [
                            "https://www.w3.org/2018/credentials/v1",
                            "https://w3id.org/security/suites/jws-2020/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/35978ca0-4336-48d3-acdc-8f7a097d938c.json",
                          "issuer": "did:web:gx-notary.gxdch.dih.telekom.com:v1",
                          "issuanceDate": "2024-01-01T02:42:34.617Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:legalRegistrationNumber",
                            "gx:vatID": "DE118645675",
                            "gx:vatID-countryCode": "DE"
                          },
                          "evidence": [
                            {
                              "gx:evidenceURL": "https://ec.europa.eu/taxation_customs/vies/services/checkVatService",
                              "gx:executionDate": "2024-01-01T02:42:34.617Z",
                              "gx:evidenceOf": "gx:vatID"
                            }
                          ],
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2024-01-01T02:42:34.635Z",
                            "proofPurpose": "assertionMethod",
                            "verificationMethod": "did:web:gx-notary.gxdch.dih.telekom.com:v1#X509-JWK2020",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..eWQFtqfP6vnA2hwo8wHMDD5w6ZkAs62wTGpBHMhi6ZFWijeZcQbQbtFerZEk5j2UDvfedSNLjFUHfhDMBNt9OA-S7QyH7uCT9Td6jNIBrJqar-bTAiABqvUNfebLs8eRtAtGr0y_vmuxNd0EUPOAINQrK85q8GxyUa3X4EGd9b7jchKW2oGl-rgsHq7OxNXvSd8OxY330rsCvmKmqyPy6ga-MDQwBZYXRDjTWMQ7Df8vmMTy8ACZMOJq_ajjjHTHxi7l_H3yD0EacfUcwDSFmH5SFjiHMiw88MBkJAAqLBj0ZlWzDClzbJqZGVLFLW-2KNRV-Eix5wu_21KIHCFl4TWouXD2kuY24ARx6HzFiklgOqhuL0x6NVTDI2pB6Z8CiQbNAusPxPAelczU56PQW6sIwo4s6fvMZWQLVTVtVDu5Z02GVYAVcBNUs9nKHAK9p4lu2PcIWg5LMOAgDYCx-tUW78XhP20YhboReNByORb15zlcGkVo-JVB0PtpdsEEBLnGg09Of6-z6moq20hcKOO0-ilrrwf3NjYB32QWKFt8XRAuxp9SayvTUVUr2nFSBoimgAr7tPkL9w-_DdOTY_9pwv5YtqnEE3ii22J7tls5eWKhaxs7hXatZqY20rWna0ccOGuEbTkHip4J-yMhy4mIJ2dzyxZHnqN8LAtWeTo"
                          }
                        },
                        {
                          "@context": [
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/2018/credentials/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/d56122d4-679e-4290-a36d-09405df912cb.json",
                          "issuer": "did:web:carla.dih-cloud.com:dev:signer_service",
                          "issuanceDate": "2024-02-01T20:16:19Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:GaiaXTermsAndConditions",
                            "gx:termsAndConditions": "The PARTICIPANT signing the Self-Description agrees as follows:\\n- to update its descriptions about any changes, be it technical, organizational, or legal - especially but not limited to contractual in regards to the indicated attributes present in the descriptions.\\n\\nThe keypair used to sign Verifiable Credentials will be revoked where Gaia-X Association becomes aware of any inaccurate statements in regards to the claims which result in a non-compliance with the Trust Framework and policy rules defined in the Policy Rules and Labelling Document (PRLD).\\n"
                          },
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2024-01-01T04:21:12.913Z",
                            "proofPurpose": "assertionMethod",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..aYEdMRMLH1E7IfcA9mccspv_oiGrrkV7KZRQWcCSHM2a2Q4omGyTy9FkHWEZ8bA87vLgcJUsaOyA1ggYDDT0OQEtXiKeEZaYkMCc5Av0iQzyiB_1Ow2g7DXzGZ4kRoFQXbhFWPLn-f3f4ihkuIdF8MO9Iijzwbju2Jon4AxN5UMEcq1vGNkAn1PsW6H6myzU9wlk287GKpMKnIcL_brJ6IbbZq7gz7tZMpkf_epMaRTftkcPg3tUhPQRgtNOJ_N1FGvSByghVLDNQGuF4nVB3ZWvab87h_DcsGZwKg9farED0AiMWuzEFV-Ft99mkwN1rslv-rm7RXQcKuuHlTVYARGFbUaV4Tb4joYr2F9IVjWNq9V4xxWt988T7ROPWAuYsy9VS9VeVTI4kvqEu-VSamHDW34Rl6tX1bE-5c0OA2Hlq41QQXsMhjLZh0vW07-jENqaykVe9IBNS8o5Sfc4WJYukwa0axQ3DGl5AlpQMF_uJwG1hJOUyaO8fifC4xNDAh_DRSpo2Wfsk1dD3HVw5IH1YKGzDRrzXz1sxRW-SSiSXrcibxr63V0hHFi2Rm96L5CvF2jD__CkmKWNGOK8d2ksQZkGNbWBcTx5hitTkAdn8dEj0lG5O178-demyC0XWHMR18e0MgMxtq0IutRO3OqNjfA-poIARrwK14NgcbM",
                            "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1"
                          }
                        }
                      ]
                    }
                    """;

    @Test
    public void testServiceOfferingWrongLegalParticipantError(@Autowired ValidityChecker validityChecker) throws JsonProcessingException {
        var selfdescriptionPostRequest = objectMapper.readValue(serviceOfferingWrongParticipantStr, SelfdescriptionPostRequest.class);
        validityChecker.validate(selfdescriptionPostRequest);
        assertEquals(selfdescriptionPostRequest, validityChecker.getValidated(() -> objectMapper.readValue(serviceOfferingWrongParticipantStr, SelfdescriptionPostRequest.class)).get());
    }

    static final String legalParticipantStr =
            """
                    {
                      "externalId": "ID01234-123-4321",
                      "type": "LegalParticipant",
                      "holder": "BPNL000000000000",
                      "name": "Legal Participant Company Name",
                      "registrationNumber": [
                        {
                          "type": "leiCode",
                          "value": "5299004XPX8GS3AHAV34"
                        }
                      ],
                      "headquarterAddress.countrySubdivisionCode": "DE-BY",
                      "legalAddress.countrySubdivisionCode": "DE-NW",
                      "attachment": [
                        {
                          "@context": [
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/2018/credentials/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/f3f01f41-e1fe-4c7e-90c7-14b06a1b4b72/db042403-602e-4447-8f18-f849b095d3ea.json",
                          "issuer": "did:web:gx-notary.gxdch.dih.telekom.com:v1",
                          "issuanceDate": "2025-02-10T20:16:19Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:GaiaXTermsAndConditions",
                            "gx:termsAndConditions": "The PARTICIPANT signing the Self-Description agrees as follows:\\n- to update its descriptions about any changes, be it technical, organizational, or legal - especially but not limited to contractual in regards to the indicated attributes present in the descriptions.\\n\\nThe keypair used to sign Verifiable Credentials will be revoked where Gaia-X Association becomes aware of any inaccurate statements in regards to the claims which result in a non-compliance with the Trust Framework and policy rules defined in the Policy Rules and Labelling Document (PRLD)\\n"
                          },
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2025-02-10T04:21:12.913Z",
                            "proofPurpose": "assertionMethod",
                            "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..aYEdMRMLH1E7IfcA9mccspv_oiGrrkV7KZRQWcCSHM2a2Q4omGyTy9FkHWEZ8bA87vLgcJUsaOyA1ggYDDT0OQEtXiKeEZaYkMCc5Av0iQzyiB_1Ow2g7DXzGZ4kRoFQXbhFWPLn-f3f4ihkuIdF8MO9Iijzwbju2Jon4AxN5UMEcq1vGNkAn1PsW6H6myzU9wlk287GKpMKnIcL_brJ6IbbZq7gz7tZMpkf_epMaRTftkcPg3tUhPQRgtNOJ_N1FGvSByghVLDNQGuF4nVB3ZWvab87h_DcsGZwKg9farED0AiMWuzEFV-Ft99mkwN1rslv-rm7RXQcKuuHlTVYARGFbUaV4Tb4joYr2F9IVjWNq9V4xxWt988T7ROPWAuYsy9VS9VeVTI4kvqEu-VSamHDW34Rl6tX1bE-5c0OA2Hlq41QQXsMhjLZh0vW07-jENqaykVe9IBNS8o5Sfc4WJYukwa0axQ3DGl5AlpQMF_uJwG1hJOUyaO8fifC4xNDAh_DRSpo2Wfsk1dD3HVw5IH1YKGzDRrzXz1sxRW-SSiSXrcibxr63V0hHFi2Rm96L5CvF2jD__CkmKWNGOK8d2ksQZkGNbWBcTx5hitTkAdn8dEj0lG5O178-demyC0XWHMR18e0MgMxtq0IutRO3OqNjfA-poIARrwK14NgcbM"
                          }
                        }
                      ]
                    }
                    """;
    static final String legalParticipantStrNoTnc =
            """
                  {
                      "externalId": "ID01234-123-4321",
                      "type": "LegalParticipant",
                      "holder": "BPNL000000000000",
                      "name": "Legal Participant Company Name",
                      "registrationNumber": [
                        {
                          "type": "leiCode",
                          "value": "5299004XPX8GS3AHAV34"
                        }
                      ],
                      "headquarterAddress.countrySubdivisionCode": "DE-BY",
                      "legalAddress.countrySubdivisionCode": "DE-NW"
                      }
                    """;

    static final String legalParticipantStrWrongTncBpn =
            """
                    {
                      "externalId": "ID01234-123-4321",
                      "type": "LegalParticipant",
                      "holder": "BPNL000000000000",
                      "name": "Legal Participant Company Name",
                      "registrationNumber": [
                        {
                          "type": "taxID",
                          "value": "o12345678"
                        }
                      ],
                      "headquarterAddress.countrySubdivisionCode": "DE",
                      "legalAddress.countrySubdivisionCode": "DE",
                      "attachment": [
                        {
                          "@context": [
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/2018/credentials/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/d56122d4-679e-4290-a36d-09405df912cb.json",
                          "issuer": "did:web:carla.dih-cloud.com:dev:signer_service",
                          "issuanceDate": "2024-02-01T20:16:19Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL0000000000023",
                            "type": "gx:GaiaXTermsAndConditions",
                            "gx:termsAndConditions": "The PARTICIPANT signing the Self-Description agrees as follows:\\n- to update its descriptions about any changes, be it technical, organizational, or legal - especially but not limited to contractual in regards to the indicated attributes present in the descriptions.\\n\\nThe keypair used to sign Verifiable Credentials will be revoked where Gaia-X Association becomes aware of any inaccurate statements in regards to the claims which result in a non-compliance with the Trust Framework and policy rules defined in the Policy Rules and Labelling Document (PRLD)\\n"
                          },
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2024-01-01T04:21:12.913Z",
                            "proofPurpose": "assertionMethod",
                            "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..aYEdMRMLH1E7IfcA9mccspv_oiGrrkV7KZRQWcCSHM2a2Q4omGyTy9FkHWEZ8bA87vLgcJUsaOyA1ggYDDT0OQEtXiKeEZaYkMCc5Av0iQzyiB_1Ow2g7DXzGZ4kRoFQXbhFWPLn-f3f4ihkuIdF8MO9Iijzwbju2Jon4AxN5UMEcq1vGNkAn1PsW6H6myzU9wlk287GKpMKnIcL_brJ6IbbZq7gz7tZMpkf_epMaRTftkcPg3tUhPQRgtNOJ_N1FGvSByghVLDNQGuF4nVB3ZWvab87h_DcsGZwKg9farED0AiMWuzEFV-Ft99mkwN1rslv-rm7RXQcKuuHlTVYARGFbUaV4Tb4joYr2F9IVjWNq9V4xxWt988T7ROPWAuYsy9VS9VeVTI4kvqEu-VSamHDW34Rl6tX1bE-5c0OA2Hlq41QQXsMhjLZh0vW07-jENqaykVe9IBNS8o5Sfc4WJYukwa0axQ3DGl5AlpQMF_uJwG1hJOUyaO8fifC4xNDAh_DRSpo2Wfsk1dD3HVw5IH1YKGzDRrzXz1sxRW-SSiSXrcibxr63V0hHFi2Rm96L5CvF2jD__CkmKWNGOK8d2ksQZkGNbWBcTx5hitTkAdn8dEj0lG5O178-demyC0XWHMR18e0MgMxtq0IutRO3OqNjfA-poIARrwK14NgcbM"
                          }
                        }
                      ]
                    }
                    """;

    static final String legalParticipantUrlStr =
            """
            {
                         "externalId": "ID01234-123-4321",
                         "type": "LegalParticipant",
                         "holder": "BPNL000000000000",
                         "name": "Legal Participant Company Name",
                         "registrationNumber": [
                             {
                                 "type": "leiCode",
                                 "value": "5299004XPX8GS3AHAV34"
                             }
                         ],
                         "headquarterAddress.countrySubdivisionCode": "DE-BY",
                         "legalAddress.countrySubdivisionCode": "DE-NW"
            }
           """;

    static final String GX_TNC = """
                            {
                          "@context": [
                            "https://w3id.org/security/suites/jws-2020/v1",
                            "https://www.w3.org/2018/credentials/v1"
                          ],
                          "type": "VerifiableCredential",
                          "id": "https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/d56122d4-679e-4290-a36d-09405df912cb.json",
                          "issuer": "did:web:carla.dih-cloud.com:dev:signer_service",
                          "issuanceDate": "2024-02-01T20:16:19Z",
                          "credentialSubject": {
                            "@context": "https://registry.lab.gaia-x.eu/development/api/trusted-shape-registry/v1/shapes/jsonld/trustframework#",
                            "id": "http://catena-x.net/bpn/BPNL000000000000",
                            "type": "gx:GaiaXTermsAndConditions",
                            "gx:termsAndConditions": "The PARTICIPANT signing the Self-Description agrees as follows:\\n- to update its descriptions about any changes, be it technical, organizational, or legal - especially but not limited to contractual in regards to the indicated attributes present in the descriptions.\\n\\nThe keypair used to sign Verifiable Credentials will be revoked where Gaia-X Association becomes aware of any inaccurate statements in regards to the claims which result in a non-compliance with the Trust Framework and policy rules defined in the Policy Rules and Labelling Document (PRLD)\\n"
                          },
                          "proof": {
                            "type": "JsonWebSignature2020",
                            "created": "2024-01-01T04:21:12.913Z",
                            "proofPurpose": "assertionMethod",
                            "verificationMethod": "did:web:gx-compliance.gxdch.dih.telekom.com:v1",
                            "jws": "eyJhbGciOiJQUzI1NiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..aYEdMRMLH1E7IfcA9mccspv_oiGrrkV7KZRQWcCSHM2a2Q4omGyTy9FkHWEZ8bA87vLgcJUsaOyA1ggYDDT0OQEtXiKeEZaYkMCc5Av0iQzyiB_1Ow2g7DXzGZ4kRoFQXbhFWPLn-f3f4ihkuIdF8MO9Iijzwbju2Jon4AxN5UMEcq1vGNkAn1PsW6H6myzU9wlk287GKpMKnIcL_brJ6IbbZq7gz7tZMpkf_epMaRTftkcPg3tUhPQRgtNOJ_N1FGvSByghVLDNQGuF4nVB3ZWvab87h_DcsGZwKg9farED0AiMWuzEFV-Ft99mkwN1rslv-rm7RXQcKuuHlTVYARGFbUaV4Tb4joYr2F9IVjWNq9V4xxWt988T7ROPWAuYsy9VS9VeVTI4kvqEu-VSamHDW34Rl6tX1bE-5c0OA2Hlq41QQXsMhjLZh0vW07-jENqaykVe9IBNS8o5Sfc4WJYukwa0axQ3DGl5AlpQMF_uJwG1hJOUyaO8fifC4xNDAh_DRSpo2Wfsk1dD3HVw5IH1YKGzDRrzXz1sxRW-SSiSXrcibxr63V0hHFi2Rm96L5CvF2jD__CkmKWNGOK8d2ksQZkGNbWBcTx5hitTkAdn8dEj0lG5O178-demyC0XWHMR18e0MgMxtq0IutRO3OqNjfA-poIARrwK14NgcbM"
                          }
                        }
                        """;

    static Stream<Arguments> legalParticipantProvider() {
        return Stream.of(
                Arguments.of(legalParticipantStr, "Legal Participant, correct TNC is in attachment"),
                Arguments.of(legalParticipantStrNoTnc, "Legal Participant, TNC is missed"),
                Arguments.of(legalParticipantStrWrongTncBpn, "Legal Participant TNC with wrong BPN"),
                Arguments.of(legalParticipantUrlStr, "Legal Participant TNC available by reference")
        );
    }

    @ParameterizedTest(name = "{index} - {1}")
    @MethodSource("legalParticipantProvider")
    public void testLegalPersonConverter(String selfdescriptionRequestStr, String type) throws JsonProcessingException, MalformedURLException {
        var url = URI.create("https://carla.dih-cloud.com/dev/compliance_service/self-descriptions/cd532ca1-2545-4ce0-942f-008aece14316/d56122d4-679e-4290-a36d-09405df912cb.json").toURL();
        try (MockedStatic<Utils> mockedStatic = Mockito.mockStatic(Utils.class)) {
            mockedStatic.when(
                    () -> Utils.getConnectionIfRedirected(eq(url), anyInt())
            ).thenReturn(new URLConnection(url) {
                @Override public InputStream getInputStream() { return new ByteArrayInputStream(GX_TNC.getBytes());}

                @Override public void connect() throws IOException {}
            });

            var selfdescriptionPostRequest = objectMapper.readValue(selfdescriptionRequestStr, SelfdescriptionPostRequest.class);
            var converted = conversionService.convert(selfdescriptionPostRequest, SelfDescription.class);
            assertNotNull(converted, "converted object should not be null");
            assertFalse(converted.getVerifiableCredentialList().isEmpty());
            assertFalse(converted.getVerifiableCredentialList().stream()
                    .flatMap(vc -> Optional.ofNullable(vc.getCredentialSubject()).stream())
                    .anyMatch(subj ->
                            "gx:GaiaXTermsAndConditions".equals(subj.getType())
                                    && URI.create("http://catena-x.net/bpn/BPNL000000000001").equals(subj.getId())
                    )
            ); // no TNC
            System.out.println(objectMapper.writeValueAsString(converted.getVerifiableCredentialList()));
        }
    }
}
