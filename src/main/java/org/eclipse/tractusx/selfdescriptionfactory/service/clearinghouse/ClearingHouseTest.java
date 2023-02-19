package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
@Slf4j
public class ClearingHouseTest extends ClearingHouse{

    public ClearingHouseTest(ObjectMapper objectMapper, KeycloakManager keycloakManager){
        super(objectMapper, keycloakManager);
    }

    @Override
    @SneakyThrows
    public void doWork(String url, Object payload) {
        log.debug("URL: {}", url);
        log.debug("Bearer: {}", keycloakManager.getKeycloack("callback").tokenManager().getAccessTokenString());
        log.debug("Payload: {}", objectMapper.writeValueAsString(payload));
    }
}
