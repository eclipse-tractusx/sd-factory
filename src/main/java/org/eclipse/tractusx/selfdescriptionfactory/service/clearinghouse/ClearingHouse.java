package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public abstract class ClearingHouse {
    @Value("${app.usersDetails.clearingHouse.uri}")
    private String clearingHouseUrl;
    @Autowired
    protected KeycloakManager keycloakManager;

    public abstract void doWork(String url, Object payload, String externalId, String token);

    public void sendToClearingHouse(VerifiableCredential verifiableCredential, String externalId) {
        doWork(clearingHouseUrl, verifiableCredential, externalId, "Bearer ".concat(keycloakManager.getKeycloack("clearingHouse").tokenManager().getAccessTokenString()));
    }
}
