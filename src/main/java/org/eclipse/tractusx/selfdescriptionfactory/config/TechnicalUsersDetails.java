package org.eclipse.tractusx.selfdescriptionfactory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "app")
@Getter @Setter
public class TechnicalUsersDetails {
    private Map<String, UserDetail> usersDetails;
    public record UserDetail (
        String serverUrl,
        String realm,
        String username,
        String password,
        String clientId,
        String clientSecret){}
}
