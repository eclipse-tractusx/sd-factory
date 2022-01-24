package com.tsystems.sdhub.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Objects;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Keystore {
    @Getter
    private byte[] pubKey;
    @Getter
    private final byte[] privKey = new byte[64];

    @Value("${app.keystore.path}")
    private String keystorePath;

    @Value("${app.keystore.passwd}")
    private String password;

    @Value("${app.keystore.alias}")
    private String alias;


    @PostConstruct
    public void init() throws Exception{
        InputStream is = null;
        try {
            if (Objects.isNull(keystorePath) || keystorePath.isBlank()) is = this.getClass().getClassLoader().getResourceAsStream("keystore");
            else is = new FileInputStream(keystorePath);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] passwd = password.toCharArray();
            keystore.load(is, passwd);
            byte[] privKeyEncoded = keystore.getKey(alias, passwd).getEncoded();
            byte[] pubKeyEncoded = keystore.getCertificate(alias).getPublicKey().getEncoded();
            pubKey = Arrays.copyOfRange(pubKeyEncoded, 12, 44);
            System.arraycopy(Arrays.copyOfRange(privKeyEncoded, 16, 48), 0, privKey, 0, 32);
            System.arraycopy(Arrays.copyOfRange(pubKeyEncoded, 12, 44), 0, privKey, 32, 32);
        } finally {
            if (Objects.nonNull(is)) is.close();
        }
    }
}
