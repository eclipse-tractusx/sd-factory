package com.tsystems.sdhub.service;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.http.media.MediaType;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.tsystems.sdhub.util.Keystore;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2018LdSigner;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class VerifiableCredentialService {

    private final Keystore keystore;

    static final URI TRACEABILITY_URI = URI.create("https://w3id.org/traceability/v1");

    @PostConstruct
    private void init() throws IOException, JsonLdError {
        try (InputStream traceabilityIs = this.getClass().getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/traceability-v1.jsonld")) {
            VerifiableCredentialContexts.CONTEXTS.put(TRACEABILITY_URI, JsonDocument.of(MediaType.JSON_LD, traceabilityIs));
        }
    }

    public VerifiableCredential createVC(Map<String, Object> claims, URI holderId, URI issuerId) throws Exception{
        CredentialSubject credentialSubject = CredentialSubject.builder()
                .id(holderId)
                .claims(claims)
                .build();
        Date issuanceDate = new Date();
        VerifiableCredential verifiableCredential = VerifiableCredential.builder()
                .context(TRACEABILITY_URI)
                .issuer(issuerId)
                .issuanceDate(issuanceDate)
                .credentialSubject(credentialSubject)
                .build();
        Ed25519Signature2018LdSigner signer = new Ed25519Signature2018LdSigner(keystore.getPrivKey());
        signer.setCreated(new Date());
        signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
        signer.setVerificationMethod(issuerId.resolve("#key"));
        signer.sign(verifiableCredential);
        return verifiableCredential;
    }

    public boolean verifySDHubVC(VerifiableCredential verifiableCredential) throws Exception{
		Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(keystore.getPubKey());
		return verifier.verify(verifiableCredential);
	}
}
