package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import foundation.identity.jsonld.JsonLDObject;
import net.catenax.sdhub.service.SDFactory;
import net.catenax.sdhub.service.VerifierService;
import org.junit.Assert;
import org.junit.Test;
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
    private SDFactory sdFactory;

    @Autowired
    private VerifierService verifierService;

    private static String HOLDER_DID =  "https://catalog.demo.supplytree.org/api/user/holder";

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
        Assert.assertTrue(verifierService.createVerifier(verifiableCredential).verifier().verify(verifiableCredential));
    }

    @Test
    public void testBadSignature() throws Exception{
        VerifiableCredential verifiableCredential = createVc();
        String representation = verifiableCredential.toJson(true);
        String tamperedRepresentation = representation.replaceFirst(HOLDER_DID, "https://catalog.demo.supplytree.org/api/user/fake-holder");
        VerifiableCredential tamperedVerifiableCredential = VerifiableCredential.fromJson(tamperedRepresentation);
        Assert.assertFalse(verifierService.createVerifier(tamperedVerifiableCredential).verifier().verify(tamperedVerifiableCredential));
    }

    private VerifiableCredential createVc() throws Exception{
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("company_number", "RU-123");
        claims.put("headquarter_country", "RU");
        claims.put("legal_country", "RU");
        claims.put("bpn", "12345678");
        return sdFactory.createVC(claims, URI.create(HOLDER_DID));
    }

    @Test
    public void testJsonLd() throws Exception{

        JsonLDObject jsonLDObject = JsonLDObject.fromJson("""
                {
                    "@context" : {
                        "company_number": "https://schema.org/taxID",
                        "headquarter.country": "https://schema.org/addressCountry",
                        "legal.country": "https://schema.org/addressCountry"
                     },
                     "company_number": " ru123",
                     "headquarter.country": "RU",
                     "legal.country": "RU"
                }
                """);
        System.out.println(jsonLDObject.toJson(true));
    }
}
