package net.catenax.sdhub.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class Keystore {

    @Value("${app.keystore.path}")
    private String keystorePath;

    @Value("${app.keystore.passwd}")
    private String password;

    private final KeystoreProperties keystoreProperties;

    public KeyInfo getPrivKey(String keyId) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreProperties.KeystoreProp keystoreProp = getKeystoreProps(keyId);
        if (Objects.isNull(keystoreProp)) throw new RuntimeException("cannot get private key for " + keyId);
        byte[] privKey = new byte[64];
        KeyStore keystore = loadKeyStore();
        byte[] privKeyEncoded = keystore.getKey(keystoreProp.getKeystoreAlias(), password.toCharArray()).getEncoded();
        byte[] pubKeyEncoded = keystore.getCertificate(keystoreProp.getKeystoreAlias()).getPublicKey().getEncoded();
        byte[] pubKey = Arrays.copyOfRange(pubKeyEncoded, 12, 44);
        System.arraycopy(Arrays.copyOfRange(privKeyEncoded, 16, 48), 0, privKey, 0, 32);
        System.arraycopy(pubKey, 0, privKey, 32, 32);
        return new KeyInfo(privKey, URI.create(keystoreProp.getDid()));
    }

    public KeyInfo getPubKey(String keyId) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        KeystoreProperties.KeystoreProp keystoreProp = getKeystoreProps(keyId);
        if (Objects.isNull(keystoreProp)) return null;
        KeyStore keystore = loadKeyStore();
        byte[] pubKeyEncoded = keystore.getCertificate(keystoreProp.getKeystoreAlias()).getPublicKey().getEncoded();
        return new KeyInfo(Arrays.copyOfRange(pubKeyEncoded, 12, 44), URI.create(keystoreProp.getDid()));
    }

    public KeyPair getKeyPair(String keyId) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreProperties.KeystoreProp keystoreProp = getKeystoreProps(keyId);
        if (Objects.isNull(keystoreProp)) throw new RuntimeException("cannot get private key for " + keyId);
        KeyStore keystore = loadKeyStore();
        Key key = keystore.getKey(keystoreProp.getKeystoreAlias(), password.toCharArray());
        if (key instanceof PrivateKey) {
            Certificate cert = keystore.getCertificate(keystoreProp.getKeystoreAlias());
            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, (PrivateKey) key);
        } else {
            return null;
        }
    }

    private KeystoreProperties.KeystoreProp getKeystoreProps(String keyId) {
        return keystoreProperties.getCatenax().getKeyId().contains(keyId) ? keystoreProperties.getCatenax()
                : keystoreProperties.getSdhub().getKeyId().contains(keyId) ? keystoreProperties.getSdhub() : null;
    }

    public KeyStore loadKeyStore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
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
