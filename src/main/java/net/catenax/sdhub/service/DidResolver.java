package net.catenax.sdhub.service;

import foundation.identity.jsonld.JsonLDObject;

import java.net.URI;

public interface DidResolver {
    JsonLDObject resolve(URI did);
}
