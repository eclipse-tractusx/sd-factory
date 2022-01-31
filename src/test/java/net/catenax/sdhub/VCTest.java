package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import net.catenax.sdhub.service.VerifiableCredentialService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VCTest {

    @Autowired
    VerifiableCredentialService verifiableCredentialService;

    @Test
    public void testVc() throws Exception{
        VerifiableCredential verifiableCredential = createVc();
        Assert.assertNotNull(verifiableCredential);
        String representation = verifiableCredential.toJson(true);
        Assert.assertFalse(representation.isBlank());
        System.out.println(representation);
    }

    @Test
    public void testGoodSignature() throws Exception{
        VerifiableCredential verifiableCredential = createVc();
        Assert.assertTrue(verifiableCredentialService.verifySDHubVC(verifiableCredential));
    }

    @Test
    public void testBadSignature() throws Exception{
        VerifiableCredential verifiableCredential = createVc();
        String representation = verifiableCredential.toJson(true);
        String tamperedRepresentation = representation.replaceFirst("https://catalog.demo.supplytree.org/api/user/sd-hub", "https://catalog.demo.supplytree.org/api/user/fake-sd-hub");
        VerifiableCredential tamperedVerifiableCredential = VerifiableCredential.fromJson(tamperedRepresentation);
        Assert.assertFalse(verifiableCredentialService.verifySDHubVC(tamperedVerifiableCredential));
    }

    private VerifiableCredential createVc() throws Exception{
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("company", "My cool company");
        claims.put("operator", "My operator");
        claims.put("region", "Germany");
        claims.put("service", "Connector");
        return verifiableCredentialService.createVC(
                claims,
                URI.create("https://catalog.demo.supplytree.org/api/user/holder"),
                URI.create("https://catalog.demo.supplytree.org/api/user/sd-hub")
        );
    }
}
