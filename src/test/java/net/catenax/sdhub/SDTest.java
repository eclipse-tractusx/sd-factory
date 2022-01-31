package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import foundation.identity.jsonld.JsonLDException;
import net.catenax.sdhub.service.DidResolver;
import net.catenax.sdhub.service.VerifiableCredentialService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SDTest {

    private String sdUrl = "https://catalog.demo.supplytree.org/api/user/5673c857d0/selfdescription";

    @Autowired
    VerifiableCredentialService verifiableCredentialService;

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
        var verifier = verifiableCredentialService.createVerifier(vp);
        Assert.assertNotNull(verifier);
        Assert.assertTrue(verifier.verifier().verify(vp));
        System.out.printf("VP is authentic and signed by %s\n", verifier.controller());
        var vc = vp.getVerifiableCredential();
        verifier = verifiableCredentialService.createVerifier(vc);
        Assert.assertNotNull(verifier);
        Assert.assertTrue(verifier.verifier().verify(vc));
        System.out.println(vc.getCredentialSubject().toJson(true));
        System.out.printf("VC is authentic and signed by %s\n", verifier.controller());
    }
}
