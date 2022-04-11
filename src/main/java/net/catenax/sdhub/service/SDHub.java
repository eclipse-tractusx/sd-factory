package net.catenax.sdhub.service;

import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.service.wallet.CustodianWallet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDHub {

    // Namespace for the Self-Description document context
    static final URI SD_VOC_URI = URI.create("https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld");
    // Namespace for the Traceability context, used for the test in conjunction with https://catalog.demo.supplytree.org project
    static final URI TRACEABILITY_VOC_URI = URI.create("https://w3id.org/traceability/v1");

    static {
        try (InputStream sdVocIs = SDHub.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/sd-document-v0.1.jsonld");
             InputStream trVocIs = SDHub.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/traceability-v1.jsonld")) {
            assert sdVocIs != null;
            VerifiableCredentialContexts.CONTEXTS.put(SD_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, sdVocIs));
            assert trVocIs != null;
            VerifiableCredentialContexts.CONTEXTS.put(TRACEABILITY_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, trVocIs));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private final CustodianWallet custodianWallet;

    public VerifiablePresentation createVP(List<VerifiableCredential> verifiableCredentialList) {
        if (verifiableCredentialList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return custodianWallet.getSignedVP(verifiableCredentialList);
    }
}
