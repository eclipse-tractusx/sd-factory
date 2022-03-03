package net.catenax.sdhub.service;

import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.suites.SignatureSuite;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2020LdVerifier;
import info.weboftrust.ldsignatures.verifier.LdVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Objects;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class VerifiableCredentialService {
    public record Verifier(
            LdVerifier<? extends SignatureSuite> verifier,
            URI controller) {}

    private final PublicKeyResolver publicKeyResolver;

    public Verifier createVerifier(JsonLDObject signedObject) throws Exception {
        var ldProof = LdProof.getFromJsonLDObject(signedObject);
        var verificationMethod = ldProof.getVerificationMethod();
        var key = publicKeyResolver.getPublicKey(verificationMethod.toString());
        var verifier = switch (ldProof.getJsonObject().get("type").toString()){
            case "Ed25519Signature2018" -> new Ed25519Signature2018LdVerifier(key.rawKey());
            case "Ed25519Signature2020" -> new Ed25519Signature2020LdVerifier(key.rawKey());
            default -> null;
        };
        return Objects.isNull(verifier) ? null : new Verifier(verifier, key.controller());
    }
}
