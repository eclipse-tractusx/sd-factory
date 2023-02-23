package org.eclipse.tractusx.selfdescriptionfactory.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.config.TechnicalUsersDetails;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KeycloakManager implements DisposableBean {
    private final TechnicalUsersDetails technicalUsersDetails;
    private final Map<String, Keycloak> keycloakMap = new HashMap<>();

    public Keycloak getKeycloack(String name) {
        var result = keycloakMap.get(name);
        if (Objects.isNull(result)){
            var details = technicalUsersDetails.getUsersDetails().get(name);
            if (Objects.isNull(details))
                return null;
            var keycloakBuilder = KeycloakBuilder.builder()
                    .serverUrl(details.serverUrl())
                    .realm(details.realm())
                    .username(details.username())
                    .password(details.password())
                    .clientId(details.clientId())
                    .clientSecret(details.clientSecret());
            if (Objects.isNull(details.username())) {
                keycloakBuilder.grantType("client_credentials");
            }
            result = keycloakBuilder.build();
            keycloakMap.put(name, result);
        }
        return result;
    }

    @Override
    public void destroy() throws Exception {
        keycloakMap.values().forEach(Keycloak::close);
    }
}
