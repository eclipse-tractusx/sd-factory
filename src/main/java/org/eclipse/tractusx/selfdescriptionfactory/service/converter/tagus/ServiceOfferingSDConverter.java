/********************************************************************************
 * Copyright (c) 2022,2025 T-Systems International GmbH
 * Copyright (c) 2022,2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory.service.converter.tagus;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import io.vavr.control.Try;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.eclipse.tractusx.selfdescriptionfactory.SelfDescription;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.tagus.ServiceOfferingSchema;
import org.eclipse.tractusx.selfdescriptionfactory.service.converter.TermsAndConditionsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

@ConfigurationProperties(prefix = "app.verifiable-credentials")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
@Profile("tagus-ctx")
public class ServiceOfferingSDConverter implements Converter<ServiceOfferingSchema, SelfDescription> {

    @Setter private Map<String, String> gaiaXDataAccountExport;
    @Setter private Map<String, String> gaiaXTermsAndConditions;
    @Setter private List<String> gaiaXDataProtectionRegime;
    @Setter @NotNull(message = "app.verifiableCredentials.gaia-x-participant-schema shall be defined in the configuration file") private URI gaiaXServiceSchema;
    @Setter @NotNull(message = "app.verifiableCredentials.gaia-x-policy shall be defined in the configuration file") private URI gaiaXPolicy;
    @Setter @Positive(message = "app.verifiableCredentials.durationDays shall be defined in the configuration file") private int durationDays;
    @Setter @NotNull(message = "app.verifiableCredentials.catena-x-ns shall be defined in the configuration file") private String catenaXNs;

    @Value("${app.maxRedirect:5}")
    private int maxRedirect;
    private final TermsAndConditionsHelper termsAndConditionsHelper;

    /**
     * Convert ServiceOfferingSchema to SelfDescription
     * @param serviceOfferingSchema the service offering schema
     * @return the self description
     */
    @Override
    public SelfDescription convert(ServiceOfferingSchema serviceOfferingSchema) {

        var attachedVc = Try.of(() -> Utils.getAttachmentVc(serviceOfferingSchema.getAttachment(), maxRedirect)).get();
        var holderId = URI.create("http://catena-x.net/bpn/".concat(serviceOfferingSchema.getHolder()));
        var providedBy = findLegalParticipantVc(holderId, attachedVc).isEmpty()? serviceOfferingSchema.getProvidedBy():findLegalParticipantVc(holderId, attachedVc).get().getId();

        // Create a self description with the external id from the service offering schema
        var selfDescription = new SelfDescription(serviceOfferingSchema.getExternalId());

        // Create a map for the service offering self description
        var serviceOfferingSD = new LinkedHashMap<String, Object>();
        serviceOfferingSD.put("id", holderId.toString());
        serviceOfferingSD.put("type", "gx:ServiceOffering");
        serviceOfferingSD.put("gx:providedBy", Map.of("id",providedBy));

        // Create a setter function to add values to the service offering self description
        var setter = new Object() {
            <T> Consumer<T> set(String fieldName) {
                return t -> serviceOfferingSD.put(fieldName, t);
            }
        };

        // Add termsAndConditions field if non-empty
        Optional.ofNullable(serviceOfferingSchema.getTermsAndConditions())
                .map(Object.class::cast)
                .or(() -> Optional.ofNullable(gaiaXTermsAndConditions))
                .ifPresent(setter.set("gx:termsAndConditions"));

        // Add policies field if non-empty, using policyUri

        Optional.ofNullable(serviceOfferingSchema.getPolicy())
                .ifPresent(setter.set("gx:policy"));
        // Add dataProtectionRegime and dataAccountExport fields
        Optional.ofNullable(serviceOfferingSchema.getDataProtectionRegime())
                .map(Collection.class::cast)
                .or(() -> Optional.ofNullable(gaiaXDataProtectionRegime))
                .ifPresent(setter.set("gx:dataProtectionRegime"));
        Optional.ofNullable(serviceOfferingSchema.getDataAccountExport())
                .map(Object.class::cast)
                .or(() -> Optional.ofNullable(gaiaXDataAccountExport))
                .ifPresent(setter.set("gx:dataAccountExport"));

        // Create a verifiable credential using the service offering self description
        var vc = VerifiableCredential.builder()
                .context(gaiaXServiceSchema)
                .id(URI.create("http://catena-x.net/service-offering/".concat(UUID.randomUUID().toString())))
                .issuanceDate(new Date())
                .expirationDate(Date.from(Instant.now().plus(Duration.ofDays(durationDays))))
                .credentialSubject(CredentialSubject.fromMap(serviceOfferingSD))
                .build();

        // Add the verifiable credential to the self description and return it
        selfDescription.getVerifiableCredentialList().add(vc);
        selfDescription.getVerifiableCredentialList().addAll(attachedVc);
        return selfDescription;
    }

    /**
     * Finds a legal participant verifiable credential based on the holder ID.
     *
     * @param holderId                The URI of the holder
     * @param verifiableCredentialList The list of verifiable credentials to search
     * @return                        An optional containing the first legal participant verifiable credential found, or empty if not found
     */
    private Optional<VerifiableCredential> findLegalParticipantVc(URI holderId, List<VerifiableCredential> verifiableCredentialList) {
        return verifiableCredentialList.stream()
                .filter(vc -> {
                    var credentialSubject = vc.getCredentialSubject();
                    return credentialSubject != null
                            && "gx:LegalParticipant".equals(credentialSubject.getType())
                            && holderId.equals(vc.getCredentialSubject().getId());
                }).findFirst();
    }

}
