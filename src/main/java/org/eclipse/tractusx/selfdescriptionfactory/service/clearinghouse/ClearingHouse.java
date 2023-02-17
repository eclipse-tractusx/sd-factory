package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClearingHouse {

    private final ObjectMapper objectMapper;

    @Value("${app.usersDetails.callback.urlLegalPerson}")
    private String legalPersonUrl;

    @Value("${app.usersDetails.callback.urlServiceOffering}")
    private String serviceOfferingUrl;

    private final KeycloakManager keycloakManager;

    @SneakyThrows
    public void sendToClearingHouse(VerifiableCredential verifiableCredential, String externalId) {
        //log.debug(Try.ofCallable(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(verifiableCredential)).get());
        String url;
        if (verifiableCredential.getTypes().contains("LegalPerson")) {
            url = legalPersonUrl;
        } else if (verifiableCredential.getTypes().contains("ServiceOffering")) {
            url = serviceOfferingUrl;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Can handle LegalPerson or ServiceOffering only"
            );
        }
        var payload = objectMapper.createObjectNode()
                .put("externalId", externalId)
                .put("status", "Confirm")
                .put("selfDescriptionDocument", objectMapper.writeValueAsString(verifiableCredential));
        WebClient.create(url).post()
                .uri(uriBuilder -> uriBuilder.queryParam("externalId", externalId).build())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> Optional.ofNullable(keycloakManager.getKeycloack("callback"))
                        .ifPresent(keycloakManager ->
                                headers.add("Authorization", "Bearer ".concat(keycloakManager.tokenManager().getAccessTokenString()))
                        ))
                .bodyValue(payload)
                .accept(MediaType.ALL)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
