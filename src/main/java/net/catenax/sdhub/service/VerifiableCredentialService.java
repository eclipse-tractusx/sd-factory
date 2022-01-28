package net.catenax.sdhub.service;

import android.util.Patterns;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import foundation.identity.jsonld.JsonLDDereferencer;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2018LdSigner;
import info.weboftrust.ldsignatures.suites.SignatureSuite;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2020LdVerifier;
import info.weboftrust.ldsignatures.verifier.LdVerifier;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.util.Keystore;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.Base58;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class VerifiableCredentialService {

    private final Keystore keystore;
    private final DidResolver didResolver;

    static final URI TRACEABILITY_URI = URI.create("https://w3id.org/traceability/v1");
    static final Map<String, Function<String, byte[]>> DECODER_MAP = Map.of(
            "publicKeyBase58", Base58::decode,
            "publicKeyBase64", Base64::decodeBase64,
            "publicKeyMultibase", io.ipfs.multibase.Multibase::decode
    );

    @PostConstruct
    private void init() throws IOException, JsonLdError {
        try (InputStream traceabilityIs = this.getClass().getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/traceability-v1.jsonld")) {
            VerifiableCredentialContexts.CONTEXTS.put(TRACEABILITY_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, traceabilityIs));
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

    public Pair<LdVerifier<? extends SignatureSuite>, URI> createVerifier(JsonLDObject signedObject) {
        var ldProof = LdProof.getFromJsonLDObject(signedObject);
        var verificationMethod = ldProof.getVerificationMethod();
        JsonLDObject keyLd;
        if (Patterns.WEB_URL.asMatchPredicate().test(verificationMethod.toString())) {
            // verificationMethod points to URL
            keyLd = WebClient.create(verificationMethod.toString())
                .get()
                .header("no-cache", Boolean.toString(true))
                .accept(new MediaType("application", "did+ld+json"))
                .retrieve()
                .bodyToMono(JsonLDObject.class)
                .block();
        } else {
            var vmStr = verificationMethod.toString();
            var signerDid = URI.create(vmStr.substring(0, vmStr.lastIndexOf("#")));
            var didDocument = didResolver.resolve(signerDid);
            keyLd = JsonLDDereferencer.findByIdInJsonLdObject(didDocument, verificationMethod, didDocument.getId());
        }
        if (Objects.isNull(keyLd)) return null;
        var controller = URI.create(keyLd.getJsonObject().get("controller").toString());
        var ks = new HashSet<>(keyLd.getJsonObject().keySet());
        ks.retainAll(DECODER_MAP.keySet());
        if (ks.size() != 1) return null;
        var encMethod = ks.iterator().next();
        byte[] key = DECODER_MAP.get(encMethod).apply(keyLd.getJsonObject().get(encMethod).toString());
        var verifier = switch (ldProof.getJsonObject().get("type").toString()){
            case "Ed25519Signature2018" -> new Ed25519Signature2018LdVerifier(key);
            case "Ed25519Signature2020" -> new Ed25519Signature2020LdVerifier(key);
            default -> null;
        };
        return Objects.isNull(verifier) ? null : Pair.of(verifier, controller);
    }
}
