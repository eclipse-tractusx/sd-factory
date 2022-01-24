package com.tsystems.sdhub;

import com.tsystems.sdhub.util.Keystore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KeystoreTest {

    @Autowired
    Keystore keystore;

    @Test
    public void keysAreLoaded(){
        Assert.assertNotNull(keystore.getPrivKey());
        Assert.assertNotNull(keystore.getPubKey());
    }
}
