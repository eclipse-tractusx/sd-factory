package net.catenax.sdhub.service;

import foundation.identity.jsonld.JsonLDObject;

public interface Signer {
    JsonLDObject getSigned(String keyId, String challenge, JsonLDObject objToSign) throws Exception;
}
