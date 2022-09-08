package net.catenax.selfdescriptionfactory.service.wallet;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.token.TokenManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CustodianWallet {

    @Value("${app.custodianWallet.uri}")
    private String uri;

    private final TokenManager tokenManager;

    public VerifiableCredential getSignedVC(VerifiableCredential objToSign) {
        return WebClient.create(uri).post()
                .uri(uriBuilder -> uriBuilder.pathSegment("credentials").build())
                .header("Authorization", "Bearer ".concat(tokenManager.getAccessTokenString()))
                .bodyValue(objToSign)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(VerifiableCredential.class)
                .block();
    }
}