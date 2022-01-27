package net.catenax.sdhub.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Keystore {

    @Value("${app.keystore.path}")
    private String keystorePath;

    @Value("${app.keystore.passwd}")
    private String password;

    @Value("${app.keystore.alias}")
    private String alias;

    @Getter
    private byte[] pubKey;

    @PostConstruct
    public void init() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        KeyStore keystore = loadKeyStore();
        byte[] pubKeyEncoded = keystore.getCertificate(alias).getPublicKey().getEncoded();
        pubKey = Arrays.copyOfRange(pubKeyEncoded, 12, 44);
    }

    public byte[] getPrivKey() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        byte[] privKey = new byte[64];
        KeyStore keystore = loadKeyStore();
        char[] passwd = password.toCharArray();
        byte[] pubKeyEncoded = keystore.getCertificate(alias).getPublicKey().getEncoded();
        byte[] privKeyEncoded = keystore.getKey(alias, passwd).getEncoded();
        System.arraycopy(Arrays.copyOfRange(privKeyEncoded, 16, 48), 0, privKey, 0, 32);
        System.arraycopy(Arrays.copyOfRange(pubKeyEncoded, 12, 44), 0, privKey, 32, 32);
        return privKey;
    }

    private KeyStore loadKeyStore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        InputStream is = null;
        try {
            if (Objects.isNull(keystorePath) || keystorePath.isBlank()) is = this.getClass().getClassLoader().getResourceAsStream("keystore");
            else is = new FileInputStream(keystorePath);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] passwd = password.toCharArray();
            keystore.load(is, passwd);
            return keystore;
        } finally {
            if (Objects.nonNull(is)) is.close();
        }
    }
}
