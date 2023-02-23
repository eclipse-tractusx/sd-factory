package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
@Slf4j
@RequiredArgsConstructor
public class ClearingHouseTest extends ClearingHouse{
    private final ObjectMapper objectMapper;
    @Override
    @SneakyThrows
    public void doWork(String url, Object payload, String externalId, String token) {
        log.debug("URL: {}", url);
        log.debug("Authorization: {}", token);
        log.debug("ExternalId: {}", externalId);
        log.debug("Payload: {}", objectMapper.writeValueAsString(payload));
    }
}
