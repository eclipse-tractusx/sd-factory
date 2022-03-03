package net.catenax.sdhub.util;

import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2018LdSigner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignerLocal implements Signer{

    private final Keystore keystore;

    @Override
    public JsonLDObject getSigned(String keyId, String challenge, JsonLDObject objToSign) throws Exception {
        Ed25519Signature2018LdSigner signer = new Ed25519Signature2018LdSigner(keystore.getPrivKey(keyId).rawKey());
        signer.setCreated(new Date());
        signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
        signer.setVerificationMethod(URI.create(keyId));
        Optional.ofNullable(challenge).ifPresent(signer::setChallenge);
        signer.sign(objToSign);
        return objToSign;
    }
}
