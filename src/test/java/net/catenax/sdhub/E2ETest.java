package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DirtiesContext
public class E2ETest {

    @Autowired
    @Getter
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    static MediaType JSON_LD_MEDIATYPE = new MediaType("application", "vc+ld+json") ;

    @Test
    public void createRetrieveRemoveTest() throws Exception {
        var sdDocumentJson = """
                {
                    "did" : "https://catalog.demo.supplytree.org/api/user/holder",
                    "company_number": "DE-123",
                    "headquarter_country" : "DE",
                    "legal_country" : "DE",
                    "bpn" : "123ABC"
                }
        """;
        var vcResp = getMockMvc().perform(post("/selfdescription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sdDocumentJson)).andExpect(status().isCreated()).andReturn().getResponse();
        Assert.assertNotNull(vcResp.getContentAsString());
        var resVC = VerifiableCredential.fromJson(vcResp.getContentAsString());
        var vcId = resVC.getId();

        var challenge = RandomStringUtils.random(32, true, true);
        var resp = getMockMvc().perform(get("/selfdescription/by-params")
                        .param("companyNumber", "DE-123")
                        .param("challenge", challenge)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Assert.assertNotNull(resp.getContentAsString());
        var resVP = VerifiablePresentation.fromJson(resp.getContentAsString());
        Assert.assertNotNull(resVP);
        Assert.assertNotNull(resVP.getVerifiableCredential());
        Assert.assertEquals(resVP.getLdProof().getChallenge(), challenge);
        Assert.assertEquals(resVC, resVP.getVerifiableCredential());

        //      Another method for getting VP

        challenge = RandomStringUtils.random(32, true, true);
        var resp1 = getMockMvc().perform(get("/selfdescription/by-id")
                        .param("id", vcId.toString())
                        .param("challenge", challenge)
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var vp1 = VerifiablePresentation.fromJson(resp1.getContentAsString());
        Assert.assertNotNull(vp1);
        Assert.assertNotNull(vp1.getVerifiableCredential());
        Assert.assertEquals(vp1.getLdProof().getChallenge(), challenge);
        Assert.assertEquals(resVC, vp1.getVerifiableCredential());

        // Get VC by its ID
         var resp2 = getMockMvc().perform(get(vcId))
                 .andExpect(status().isOk())
                 .andReturn()
                 .getResponse();
         var vc = VerifiableCredential.fromJson(resp2.getContentAsString());
         Assert.assertEquals(resVC, vc);

         getMockMvc().perform(delete("/selfdescription/by-id")
                 .param("id", vcId.toString())
         ).andExpect(status().isOk());
         // VC is removed
         getMockMvc().perform(get(vcId))
                 .andExpect(status().isNotFound());
    }

    @Test
    public void tryToSaveUntrustedFailedTest() throws Exception {
        var sdUrl = "https://catalog.demo.supplytree.org/api/user/5673c857d0/selfdescription";
        var vp = WebClient.create(sdUrl)
                .get()
                .header("no-cache", Boolean.toString(true))
                .accept(JSON_LD_MEDIATYPE)
                .retrieve()
                .bodyToMono(VerifiablePresentation.class)
                .block();
        Assert.assertNotNull(vp);
        var untrustedVC = vp.getVerifiableCredential();
        getMockMvc().perform(post("/selfdescription/vc")
                .contentType(JSON_LD_MEDIATYPE)
                .content(untrustedVC.toJson())).andExpect(status().isForbidden());
    }
}
