package net.catenax.sdhub.service;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.util.KeystoreProperties;
import net.catenax.sdhub.util.Signer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDFactory {

    static final URI TRACEABILITY_URI = URI.create("https://w3id.org/traceability/v1");
    static {
        try (InputStream traceabilityIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/traceability-v1.jsonld")) {
            VerifiableCredentialContexts.CONTEXTS.put(TRACEABILITY_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, traceabilityIs));
        } catch (JsonLdError | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final KeystoreProperties keystoreProperties;
    private final Signer signer;

    public VerifiableCredential createVC(Map<String, Object> claims, URI holderId) throws Exception {
        CredentialSubject credentialSubject = CredentialSubject.builder()
                .id(holderId)
                .claims(claims)
                .build();
        Date issuanceDate = new Date();
        VerifiableCredential verifiableCredential = VerifiableCredential.builder()
                .context(TRACEABILITY_URI)
                .issuer(URI.create(keystoreProperties.getCatenax().getDid()))
                .issuanceDate(issuanceDate)
                .credentialSubject(credentialSubject)
                .build();
        return (VerifiableCredential) signer.getSigned(keystoreProperties.getCatenax().getKeyId().iterator().next(), null, verifiableCredential);
    }
}
