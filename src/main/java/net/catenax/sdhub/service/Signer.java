package net.catenax.sdhub.service;

import foundation.identity.jsonld.JsonLDObject;

/**
 * Signs a JSON-LD document
 */
public interface Signer {
    /**
     * Returns signed JSON-LD document
     * @param keyId the key identifier for the signing
     * @param challenge a random string to be included to the proof. Can be null if not needed
     * @param objToSign JSON-LD document to be signed
     * @return
     * @throws Exception
     */
    JsonLDObject getSigned(String keyId, String challenge, JsonLDObject objToSign) throws Exception;
}
