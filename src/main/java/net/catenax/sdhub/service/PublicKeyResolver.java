package net.catenax.sdhub.service;

import net.catenax.sdhub.util.KeyInfo;

public interface PublicKeyResolver {
    KeyInfo getPublicKey(String keyId) throws Exception;
}
