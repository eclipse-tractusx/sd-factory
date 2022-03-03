package net.catenax.sdhub.service.impl;

import android.util.Patterns;
import foundation.identity.jsonld.JsonLDDereferencer;
import foundation.identity.jsonld.JsonLDObject;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.service.DidResolver;
import net.catenax.sdhub.service.PublicKeyResolver;
import net.catenax.sdhub.util.DecoderMap;
import net.catenax.sdhub.util.KeyInfo;
import net.catenax.sdhub.util.Keystore;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PublicKeyResolverImpl implements PublicKeyResolver {
    private final DidResolver didResolver;
    private final Keystore keystore;
    private final DecoderMap decoderMap;

    @Override
    public KeyInfo getPublicKey(String keyId) throws Exception{
        var localKey = keystore.getPubKey(keyId);
        if (Objects.nonNull(localKey)) return localKey;
        JsonLDObject keyLd;
        if (Patterns.WEB_URL.asMatchPredicate().test(keyId)) {
            // verificationMethod points to URL
            keyLd = WebClient.create(keyId)
                    .get()
                    .header("no-cache", Boolean.toString(true))
                    .accept(new MediaType("application", "did+ld+json"))
                    .retrieve()
                    .bodyToMono(JsonLDObject.class)
                    .block();
        } else {
            var signerDid = URI.create(keyId.substring(0, keyId.lastIndexOf("#")));
            var didDocument = didResolver.resolve(signerDid);
            keyLd = JsonLDDereferencer.findByIdInJsonLdObject(didDocument, URI.create(keyId), didDocument.getId());
        }
        if (Objects.isNull(keyLd)) return null;
        var ks = new HashSet<>(keyLd.getJsonObject().keySet());
        ks.retainAll(decoderMap.keySet());
        if (ks.size() != 1) return null;
        var encMethod = ks.iterator().next();
        return new KeyInfo(
                decoderMap.get(encMethod).apply(keyLd.getJsonObject().get(encMethod).toString()),
                URI.create(keyLd.getJsonObject().get("controller").toString())
        );
    }
}
