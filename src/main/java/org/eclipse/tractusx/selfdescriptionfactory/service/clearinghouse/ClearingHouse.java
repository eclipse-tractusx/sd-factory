package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
public abstract class ClearingHouse {

    protected final ObjectMapper objectMapper;

    @Value("${app.usersDetails.callback.urlLegalPerson}")
    private String legalPersonUrl;

    @Value("${app.usersDetails.callback.urlServiceOffering}")
    private String serviceOfferingUrl;

    protected final KeycloakManager keycloakManager;

    public abstract void doWork(String url, Object payload);

    @SneakyThrows
    public void sendToClearingHouse(VerifiableCredential verifiableCredential, String externalId) {
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
        doWork(url, payload);
    }
}
