package net.catenax.sdhub.service;

import foundation.identity.jsonld.JsonLDObject;

import java.net.URI;

/**
 * DID reference resolver
 */
public interface DidResolver {
    /**
     * Resolves DID reference to a DID JSON-LD document
     * @param did a reference
     * @return JSON_LD document for resolved DID
     */
    JsonLDObject resolve(URI did);
}
