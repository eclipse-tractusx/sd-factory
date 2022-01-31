package net.catenax.sdhub;

import net.catenax.sdhub.util.Keystore;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KeystoreTest {

    @Autowired
    Keystore keystore;

    @Test
    public void keysAreLoaded() throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        Assert.assertNotNull(keystore.getPrivKey());
        Assert.assertNotNull(keystore.getPubKey());
    }
}
