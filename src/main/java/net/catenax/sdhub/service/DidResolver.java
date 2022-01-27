package net.catenax.sdhub.service;

import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.suites.SignatureSuite;
import info.weboftrust.ldsignatures.verifier.LdVerifier;

import java.net.URI;

public interface DidResolver {
    JsonLDObject resolve(URI did);

    JsonLDObject resolveKey(URI did, URI key);

    LdVerifier<? extends SignatureSuite> createVerifier(URI did, URI keyId);
}
