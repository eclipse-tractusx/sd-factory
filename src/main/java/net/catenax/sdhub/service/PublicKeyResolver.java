package net.catenax.sdhub.service;

import net.catenax.sdhub.util.KeyInfo;

/**
 * Public key resolver
 */
public interface PublicKeyResolver {
    /**
     * Resolves public key by its reference
     * @param keyId the identifier of the key (e.g. verification method property)
     * @return raw key as byte array and corresponding Controller
     * @throws Exception
     */
    KeyInfo getPublicKey(String keyId) throws Exception;
}
