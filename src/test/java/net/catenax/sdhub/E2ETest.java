package net.catenax.sdhub;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class E2ETest {

    @Autowired
    @Getter
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void getByParams() throws Exception {
        var sdDocumentJson = """
                {
                    "did" : "https://catalog.demo.supplytree.org/api/user/holder",
                    "company_number": "RU-123",
                    "headquarter_country" : "RU",
                    "legal_country" : "RU",
                    "bpn" : "123ABC"
                }          
                """;
        //mongoTemplate.save(BasicDBObject.parse(vc), sdCollectionName);

        getMockMvc().perform(post("/selfdescription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sdDocumentJson)).andExpect(status().isOk());

        var challenge = RandomStringUtils.random(32, true, true);
        var resp = getMockMvc().perform(get("/selfdescription/by-params")
                        .param("companyNumber", "RU-123")
                        .param("challenge", challenge)
                )
                .andReturn()
                .getResponse();
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertNotNull(resp.getContentAsString());
        var resVP = VerifiablePresentation.fromJson(resp.getContentAsString());
        Assert.assertNotNull(resVP);
        Assert.assertNotNull(resVP.getVerifiableCredential());
        Assert.assertEquals(resVP.getLdProof().getChallenge(), challenge);
        System.out.println(resVP.toJson(true));
    }
}
