package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@ConditionalOnMissingBean(ClearingHouseTest.class)
@Service
public class ClearingHouseRemote extends ClearingHouse{
    public ClearingHouseRemote(ObjectMapper objectMapper, KeycloakManager keycloakManager){
        super(objectMapper, keycloakManager);
    }

    @Override
    public void doWork(String url, Object payload) {
        WebClient.create(url).post()
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
