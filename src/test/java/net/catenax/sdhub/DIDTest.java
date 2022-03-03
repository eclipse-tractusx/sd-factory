package net.catenax.sdhub;


import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import foundation.identity.jsonld.JsonLDDereferencer;
import io.ipfs.multibase.Base58;
import net.catenax.sdhub.service.DidResolver;
import net.catenax.sdhub.util.Keystore;
import net.catenax.sdhub.util.KeystoreProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DIDTest {

    @Autowired
    private Keystore keystore;

    @Autowired
    private DidResolver didResolver;

    @Autowired
    private KeystoreProperties keystoreProperties;

    @Test
    public void createDidTest() throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
        URI did = URI.create(keystoreProperties.getCatenax().getDid());

        Service service = Service.builder()
                .type("ServiceEndpointProxyService")
                .serviceEndpoint("https://myservice.com/myendpoint")
                .build();

        VerificationMethod verificationMethod = VerificationMethod.builder()
                .id(URI.create(did + "#key-1"))
                .type("Ed25519VerificationKey2018")
                .publicKeyBase58(Base58.encode(keystore.getPubKey(keystoreProperties.getCatenax().getKeyId().iterator().next()).rawKey()))
                .build();

        DIDDocument diddoc = DIDDocument.builder()
                .id(did)
                .service(service)
                .verificationMethod(verificationMethod)
                .build();

        System.out.println(diddoc.toJson(true));
    }

    @Test
    public void resolveDid1Test() {
        var didDocument = didResolver.resolve(URI.create("did:web:vc.transmute.world"));
        Assert.assertNotNull(didDocument);
    }

    @Test
    public void resolveDid2Test() {
        var didDocument = didResolver.resolve(URI.create("https://catalog.demo.supplytree.org/api/user/5673c857d0"));
        Assert.assertNotNull(didDocument);
    }

    @Test
    public void resolveDid3Test() {
        var didDocument = didResolver.resolve(URI.create("did:web:catalog.demo.supplytree.org:api:user:52d92e5904"));
        var keyLd = JsonLDDereferencer.findByIdInJsonLdObject(didDocument, URI.create("#key"), didDocument.getId());
    }
}
