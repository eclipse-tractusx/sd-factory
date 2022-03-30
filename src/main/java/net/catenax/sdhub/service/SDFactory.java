package net.catenax.sdhub.service;

import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.util.KeystoreProperties;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDFactory {

    // Namespace for the Self-Description document context
    static final URI SD_VOC_URI = URI.create("https://catena-x.net/selfdescription");
    // Namespace for the Traceability context, used for the test in conjunction with https://catalog.demo.supplytree.org project
    static final URI TRACEABILITY_VOC_URI = URI.create("https://w3id.org/traceability/v1");

    static {
        try (InputStream sdVocIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/sd-document-v0.1.jsonld");
            InputStream trVocIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/traceability-v1.jsonld")) {
            assert sdVocIs != null;
            VerifiableCredentialContexts.CONTEXTS.put(SD_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, sdVocIs));
            assert trVocIs != null;
            VerifiableCredentialContexts.CONTEXTS.put(TRACEABILITY_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, trVocIs));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final KeystoreProperties keystoreProperties;
    private final Signer signer;

    /**
     * Creates VerifiablePresentation from the list of VerifiableCredential and given challenge
     * @param verifiableCredentialList VerifiableCredentials to be included to the VerifiablePresentation
     * @param challenge a random string included to the proof
     * @return VerifiablePresentation signed by SD-hub private key
     * @throws Exception
     */
    public VerifiablePresentation createVP(List<VerifiableCredential> verifiableCredentialList, String challenge) throws Exception {
        var verifiablePresentationBuilder = VerifiablePresentation.builder()
                .holder(URI.create(keystoreProperties.getSdhub().getDid()));
        VerifiablePresentation verifiablePresentation;
        if (verifiableCredentialList.size() == 1) {
            verifiablePresentation = verifiablePresentationBuilder.verifiableCredential(verifiableCredentialList.iterator().next()).build();
        } else {
            verifiablePresentation = verifiablePresentationBuilder.build();
            verifiableCredentialList.forEach(vc->vc.addToJsonLDObjectAsJsonArray(verifiablePresentation));
        }
        return (VerifiablePresentation) signer.getSigned(keystoreProperties.getSdhub().getKeyId().iterator().next(), challenge, verifiablePresentation);
    }
}
