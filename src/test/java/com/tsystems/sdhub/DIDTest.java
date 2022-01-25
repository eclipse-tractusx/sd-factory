package com.tsystems.sdhub;


import com.tsystems.sdhub.util.Keystore;
import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import io.ipfs.multibase.Base58;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DIDTest {

    @Autowired
    private Keystore keystore;

    @Test
    public void didTest() {
        URI did = URI.create("did:ex:1234");

        Service service = Service.builder()
                .type("ServiceEndpointProxyService")
                .serviceEndpoint("https://myservice.com/myendpoint")
                .build();

        VerificationMethod verificationMethod = VerificationMethod.builder()
                .id(URI.create(did + "#key-1"))
                .type("Ed25519VerificationKey2018")
                .publicKeyBase58(Base58.encode(keystore.getPubKey()))
                .build();

        DIDDocument diddoc = DIDDocument.builder()
                .id(did)
                .service(service)
                .verificationMethod(verificationMethod)
                .build();

        System.out.println(diddoc.toJson(true));
    }
}
