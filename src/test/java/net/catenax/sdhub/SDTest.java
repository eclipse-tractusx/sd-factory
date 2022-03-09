package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import net.catenax.sdhub.service.DidResolver;
import net.catenax.sdhub.service.SDFactory;
import net.catenax.sdhub.service.VerifierService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SDTest {

    private final String sdUrl = "https://catalog.demo.supplytree.org/api/user/5673c857d0/selfdescription";

    @Autowired
    VerifierService verifierService;

    @Autowired
    DidResolver didResolver;

    @Autowired
    SDFactory sdFactory;

    @Test
    public void validateSDTest() throws Exception {
        var vp = WebClient.create(sdUrl)
                .get()
                .header("no-cache", Boolean.toString(true))
                .accept(new MediaType("application", "did+ld+json"))
                .retrieve()
                .bodyToMono(VerifiablePresentation.class)
                .block();
        Assert.assertNotNull(vp);
        var verifier = verifierService.createVerifier(vp);
        Assert.assertNotNull(verifier);
        Assert.assertTrue(verifier.verifier().verify(vp));
        System.out.printf("VP is authentic and signed by %s\n", verifier.controller());
        var vc = vp.getVerifiableCredential();
        verifier = verifierService.createVerifier(vc);
        Assert.assertNotNull(verifier);
        Assert.assertTrue(verifier.verifier().verify(vc));
        System.out.println(vc.getCredentialSubject().toJson(true));
        System.out.printf("VC is authentic and signed by %s\n", verifier.controller());
    }


    @Test
    public void VPCreateTest() throws Exception {
        var vcList = List.of(
                createVc(URI.create("https://catalog.demo.supplytree.org/api/user/holder1")),
                createVc(URI.create("https://catalog.demo.supplytree.org/api/user/holder2")),
                createVc(URI.create("https://catalog.demo.supplytree.org/api/user/holder3"))
        );
        var vp = sdFactory.createVP(vcList, RandomStringUtils.random(32, true, true));
        System.out.println(vp.toJson(true));
    }

    private VerifiableCredential createVc(URI holderDid) throws Exception{
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("company_number", "RU-123");
        claims.put("headquarter_country", "RU");
        claims.put("legal_country", "RU");
        claims.put("bpn", "12345678");
        return sdFactory.createVC(claims, holderDid);
    }
}
