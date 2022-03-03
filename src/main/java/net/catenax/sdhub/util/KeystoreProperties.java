package net.catenax.sdhub.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("app.wallet")
@Data
public class KeystoreProperties {

    private KeystoreProp catenax;
    private KeystoreProp sdhub;

    @Data
    public static class KeystoreProp {
        private String did;
        private List<String> keyId;
        private String keystoreAlias;
    }
}
