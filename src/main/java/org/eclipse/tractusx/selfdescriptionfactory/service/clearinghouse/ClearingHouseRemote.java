package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@ConditionalOnMissingBean(ClearingHouseTest.class)
@Service
@Slf4j
@RequiredArgsConstructor
public class ClearingHouseRemote extends ClearingHouse{
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void doWork(String url, Object payload, String externalId, String token) {
        try {
            WebClient.create(url).post()
                    .uri(uriBuilder -> uriBuilder.queryParam("externalId", externalId).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> Optional.ofNullable(token)
                            .ifPresent(keycloakManager ->
                                    headers.add(HttpHeaders.AUTHORIZATION, token)
                            ))
                    .bodyValue(payload)
                    .accept(MediaType.ALL)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception exception) {
            log.debug("payload: {}", objectMapper.writeValueAsString(payload));
            throw exception;
        }
    }
}
