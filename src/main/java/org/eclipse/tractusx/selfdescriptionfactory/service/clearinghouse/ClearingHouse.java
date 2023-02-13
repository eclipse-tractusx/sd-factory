package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClearingHouse {

    @Value("${app.clearingHouse.uri")
    private String uri; // uri of Clearing House endpoint

    public void sendToClearingHouse(VerifiableCredential verifiableCredential, String externalId) {
        WebClient.create(uri).post()
                .uri(uriBuilder -> uriBuilder.pathSegment("credentials")
                        .queryParam("externalId", externalId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(verifiableCredential)
                .accept(MediaType.ALL)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
