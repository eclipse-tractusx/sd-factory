package net.catenax.sdhub.service.impl;

import android.util.Patterns;
import foundation.identity.jsonld.JsonLDObject;
import net.catenax.sdhub.service.DidResolver;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.Base58;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
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
    public JsonLDObject resolve(URI id) {
        boolean isIdUrl = Patterns.WEB_URL.asMatchPredicate().test(id.toString());
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = WebClient.create(isIdUrl ? id.toString() : resolverUrl).get();
        if (!isIdUrl) {
            requestHeadersUriSpec = (WebClient.RequestHeadersUriSpec<?>) requestHeadersUriSpec
                    .uri(uriBuilder -> uriBuilder
                            .pathSegment("identifiers", id.toString())
                            .build()
                    );
        }
        return requestHeadersUriSpec.header("no-cache", Boolean.toString(noCache))
                .accept(new MediaType("application", "did+ld+json"))
                .retrieve()
                .bodyToMono(JsonLDObject.class)
                .block();
    }
}
