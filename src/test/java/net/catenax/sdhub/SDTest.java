package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import foundation.identity.jsonld.JsonLDException;
import net.catenax.sdhub.service.DidResolver;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SDTest {

    private String sdUrl = "https://catalog.demo.supplytree.org/api/user/5673c857d0/selfdescription";

    @Autowired
    DidResolver didResolver;

    @Test
    public void validateSDTest() throws JsonLDException, GeneralSecurityException, IOException {
        var vp = WebClient.create(sdUrl)
                .get()
                .header("no-cache", Boolean.toString(true))
                .accept(new MediaType("application", "did+ld+json"))
                .retrieve()
                .bodyToMono(VerifiablePresentation.class)
                .block();
        var keyRef = URI.create("did:web:catalog.demo.supplytree.org:api:user:5673c857d0#key");
        var holderId = URI.create("did:web:catalog.demo.supplytree.org:api:user:5673c857d0");
        var verifier = didResolver.createVerifier(holderId, keyRef);
        Assert.assertTrue(verifier.verify(vp));
    }
}
