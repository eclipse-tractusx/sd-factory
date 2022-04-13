package net.catenax.sdhub.service.wallet;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import foundation.identity.jsonld.JsonLDObject;
import foundation.identity.jsonld.JsonLDUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustodianWallet {

    @Value("${app.custodianWallet.uri}")
    private String uri;
    @Value("${app.sdhubId}")
    private String sdHubId;

    private final TokenManager tokenManager;

    public VerifiablePresentation getSignedVP(List<VerifiableCredential> verifiableCredentialList) {
        var body = new JsonLDObject();
        JsonLDUtils.jsonLdAdd(body, "holderIdentifier", sdHubId);
        JsonLDUtils.jsonLdAddAsJsonArray(body, "verifiableCredentials", verifiableCredentialList);

        return WebClient.create(uri).post()
                .uri(uriBuilder -> uriBuilder.pathSegment("presentations").build())
                .header("Authorization", "Bearer ".concat(tokenManager.getAccessTokenString()))
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(VerifiablePresentation.class)
                .block();
    }
}