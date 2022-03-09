package net.catenax.sdhub.util;

import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.Base58;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.function.Function;

/**
 * A tool to decode a key based on the provided key format.
 * Not full: a limited number of formats is supported currently.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DecoderMap extends HashMap<String, Function<String, byte[]>> {
    @PostConstruct
    public void init() {
        put("publicKeyBase58", Base58::decode);
        put("publicKeyBase64", Base64::decodeBase64);
        put("publicKeyMultibase", io.ipfs.multibase.Multibase::decode);
    }
}
