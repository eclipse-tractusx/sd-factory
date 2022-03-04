package net.catenax.sdhub.service;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.util.KeystoreProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDFactory {

    static final URI SD_VOC_URI = URI.create("https://catena-x.net/selfdescription");
    static {
        try (InputStream sdVocIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/sd-document-v0.1.jsonld")) {
            VerifiableCredentialContexts.CONTEXTS.put(SD_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, sdVocIs));
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
                .context(SD_VOC_URI)
                .issuer(URI.create(keystoreProperties.getCatenax().getDid()))
                .issuanceDate(issuanceDate)
                .type("SD-document")
                .credentialSubject(credentialSubject)
                .build();
        return (VerifiableCredential) signer.getSigned(keystoreProperties.getCatenax().getKeyId().iterator().next(), null, verifiableCredential);
    }

    public VerifiablePresentation createVP(List<VerifiableCredential> verifiableCredentialList, String challenge) throws Exception {
        VerifiablePresentation verifiablePresentation = VerifiablePresentation.builder()
                .holder(URI.create(keystoreProperties.getSdhub().getDid()))
                .build();
        verifiableCredentialList.forEach(vc->vc.addToJsonLDObjectAsJsonArray(verifiablePresentation));
        return (VerifiablePresentation) signer.getSigned(keystoreProperties.getSdhub().getKeyId().iterator().next(), challenge, verifiablePresentation);
    }
}
