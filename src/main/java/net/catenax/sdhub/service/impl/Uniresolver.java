package net.catenax.sdhub.service.impl;

import foundation.identity.jsonld.JsonLDDereferencer;
import foundation.identity.jsonld.JsonLDObject;
import info.weboftrust.ldsignatures.suites.SignatureSuite;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2020LdVerifier;
import info.weboftrust.ldsignatures.verifier.LdVerifier;
import net.catenax.sdhub.service.DidResolver;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.Base58;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

@Service
public class Uniresolver implements DidResolver {

    @Value("${uniresolver.url:https://dev.uniresolver.io/1.0}")
    private String resolverUrl;

    @Value("${uniresolver.noCache:true}")
    private boolean noCache;

    static final Map<String, Function<String, byte[]>> DECODER_MAP = Map.of(
            "publicKeyBase58", Base58::decode,
            "publicKeyBase64", Base64::decodeBase64,
            "publicKeyMultibase", io.ipfs.multibase.Multibase::decode
    );

    @Override
    public JsonLDObject resolve(URI did) {
        return WebClient.create(resolverUrl)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("identifiers", did.toString())
                        .build()
                ).header("no-cache", Boolean.toString(noCache))
                .accept(new MediaType("application", "did+ld+json"))
                .retrieve()
                .bodyToMono(JsonLDObject.class)
                .block();
    }

    @Override
    public JsonLDObject resolveKey(URI did, URI keyId) {
        var didDocument = resolve(did);
        return JsonLDDereferencer.findByIdInJsonLdObject(didDocument, keyId, didDocument.getId());
    }



    @Override
    public LdVerifier<? extends SignatureSuite> createVerifier(URI did, URI keyId) {
        var didDocument = resolve(did);
        var jsonLd = JsonLDDereferencer.findByIdInJsonLdObject(didDocument, keyId, didDocument.getId());
        var ks = new HashSet<>(jsonLd.getJsonObject().keySet());
        ks.retainAll(DECODER_MAP.keySet());
        var encMethod = ks.iterator().next();
        byte[] key = DECODER_MAP.get(encMethod).apply(jsonLd.getJsonObject().get(encMethod).toString());
        return switch (jsonLd.getJsonObject().get("type").toString()){
            case "Ed25519VerificationKey2018" -> new Ed25519Signature2018LdVerifier(key);
            case "Ed25519VerificationKey2020" -> new Ed25519Signature2020LdVerifier(key);
            default -> null;
        };
    }


}
