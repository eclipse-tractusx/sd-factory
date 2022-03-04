package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.JsonLDObject;
import lombok.Getter;
import net.catenax.sdhub.dto.GetSelfDescriptionRequest;
import net.catenax.sdhub.repo.DBCredentialSubject;
import net.catenax.sdhub.repo.DBVerifiableCredential;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class E2ETest {

    String validSelfDescr = """           
            {
               "@context":[
                  "https://www.w3.org/2018/credentials/v1"
               ],
               "type":[
                  "VerifiablePresentation"
               ],
               "verifiableCredential":[
                  {
                     "@context":[
                        "https://www.w3.org/2018/credentials/v1",
                        "https://w3id.org/traceability/v1"
                     ],
                     "id":"1643307764492",
                     "type":[
                        "VerifiableCredential"
                     ],
                     "issuer":"https://catalog.demo.supplytree.org/api/user/52d92e5904",
                     "issuanceDate":"2010-01-01T19:23:24Z",
                     "credentialSubject":{
                        "id":"did:web:catalog.demo.supplytree.org:api:user:5673c857d0",
                        "type":[
                           "LEIentity"
                        ],
                        "legalName":"Test Inc"
                     },
                     "proof":{
                        "type":"Ed25519Signature2018",
                        "created":"2022-01-27T18:22:44Z",
                        "verificationMethod":"https://catalog.demo.supplytree.org/api/user/52d92e5904/key",
                        "proofPurpose":"assertionMethod",
                        "jws":"eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..kvm1VXpAkBN63LHx9KPnX4liEuBlGmulhHHtcXc6c1QxCPt_5vTmJOsZ6mciXB7KQYgjDGVqSG45gB3y43qZCQ"
                     }
                  }
               ],
               "id":"1643307778671",
               "proof":{
                  "type":"Ed25519Signature2018",
                  "created":"2022-01-27T18:22:58Z",
                  "verificationMethod":"https://catalog.demo.supplytree.org/api/user/5673c857d0/key",
                  "proofPurpose":"authentication",
                  "challenge":"123",
                  "jws":"eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..xrWSP_xyw3OP5t5rvdSgbyN68wtwyTDg34rhGGBPHJBnU7gCqFSOF_CBhmq72pM7wHQK8j2pyntU6QBhl-2JBg"
               }
            }
            """;


    String badVc = """           
            {
               "@context":[
                  "https://www.w3.org/2018/credentials/v1"
               ],
               "type":[
                  "VerifiablePresentation"
               ],
               "verifiableCredential":[
                  {
                     "@context":[
                        "https://www.w3.org/2018/credentials/v1",
                        "https://w3id.org/traceability/v1"
                     ],
                     "id":"1643307764492",
                     "type":[
                        "VerifiableCredential"
                     ],
                     "issuer":"https://catalog.demo.supplytree.org/api/user/52d92e5904",
                     "issuanceDate":"2010-01-01T19:23:24Z",
                     "credentialSubject":{
                        "id":"did:web:catalog.demo.supplytree.org:api:user:5673c857d0",
                        "type":[
                           "LEIentity"
                        ],
                        "legalName":"Hacked"
                     },
                     "proof":{
                        "type":"Ed25519Signature2018",
                        "created":"2022-01-27T18:22:44Z",
                        "verificationMethod":"https://catalog.demo.supplytree.org/api/user/52d92e5904/key",
                        "proofPurpose":"assertionMethod",
                        "jws":"eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..kvm1VXpAkBN63LHx9KPnX4liEuBlGmulhHHtcXc6c1QxCPt_5vTmJOsZ6mciXB7KQYgjDGVqSG45gB3y43qZCQ"
                     }
                  }
               ],
               "id":"1643307778671",
               "proof":{
                  "type":"Ed25519Signature2018",
                  "created":"2022-01-27T18:22:58Z",
                  "verificationMethod":"https://catalog.demo.supplytree.org/api/user/5673c857d0/key",
                  "proofPurpose":"authentication",
                  "challenge":"123",
                  "jws":"eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..xrWSP_xyw3OP5t5rvdSgbyN68wtwyTDg34rhGGBPHJBnU7gCqFSOF_CBhmq72pM7wHQK8j2pyntU6QBhl-2JBg"
               }
            }
            """;

    String badChallengeInVpSignature = """           
            {
               "@context":[
                  "https://www.w3.org/2018/credentials/v1"
               ],
               "type":[
                  "VerifiablePresentation"
               ],
               "verifiableCredential":[
                  {
                     "@context":[
                        "https://www.w3.org/2018/credentials/v1",
                        "https://w3id.org/traceability/v1"
                     ],
                     "id":"1643307764492",
                     "type":[
                        "VerifiableCredential"
                     ],
                     "issuer":"https://catalog.demo.supplytree.org/api/user/52d92e5904",
                     "issuanceDate":"2010-01-01T19:23:24Z",
                     "credentialSubject":{
                        "id":"did:web:catalog.demo.supplytree.org:api:user:5673c857d0",
                        "type":[
                           "LEIentity"
                        ],
                        "legalName":"Test Inc"
                     },
                     "proof":{
                        "type":"Ed25519Signature2018",
                        "created":"2022-01-27T18:22:44Z",
                        "verificationMethod":"https://catalog.demo.supplytree.org/api/user/52d92e5904/key",
                        "proofPurpose":"assertionMethod",
                        "jws":"eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..kvm1VXpAkBN63LHx9KPnX4liEuBlGmulhHHtcXc6c1QxCPt_5vTmJOsZ6mciXB7KQYgjDGVqSG45gB3y43qZCQ"
                     }
                  }
               ],
               "id":"1643307778671",
               "proof":{
                  "type":"Ed25519Signature2018",
                  "created":"2022-01-27T18:22:58Z",
                  "verificationMethod":"https://catalog.demo.supplytree.org/api/user/5673c857d0/key",
                  "proofPurpose":"authentication",
                  "challenge":"124",
                  "jws":"eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..xrWSP_xyw3OP5t5rvdSgbyN68wtwyTDg34rhGGBPHJBnU7gCqFSOF_CBhmq72pM7wHQK8j2pyntU6QBhl-2JBg"
               }
            }
            """;

    @Autowired
    @Getter
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    @Value("${app.db.sd.collectionName}")
    private String sdCollectionName;

    @Test
    public void saveAndRetriveValidSdTest() throws Exception {
        var createAction = getMockMvc().perform(post("/selfdescription")
                .content(validSelfDescr)
                .contentType(new MediaType("application", "vp+ld+json"))
        );
        createAction.andExpect(MockMvcResultMatchers.status().isOk());
        var getAction = getMockMvc().perform(get("/selfdescription/id/1643307778671")
                .accept(new MediaType("application", "vp+ld+json"))
        );
        getAction.andExpect(MockMvcResultMatchers.status().isOk());
        var result = getAction.andReturn().getResponse().getContentAsString();
        var resultLd = JsonLDObject.fromJson(result);

        Assert.assertEquals(JsonLDObject.fromJson(validSelfDescr), resultLd);
    }

    @Test
    public void trySaveBadSd1Test() throws Exception {
        var createAction = getMockMvc().perform(post("/selfdescription")
                .content(badChallengeInVpSignature)
                .contentType(new MediaType("application", "vp+ld+json"))
        );
        createAction.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void trySaveBadSd2Test() throws Exception {
        var createAction = getMockMvc().perform(post("/selfdescription")
                .content(badVc)
                .contentType(new MediaType("application", "vp+ld+json"))
        );
        createAction.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getByParams() throws Exception {
        var vc = DBVerifiableCredential.builder()
                .context(List.of("https://www.w3.org/2018/credentials/v1", "https://catena-x.net/selfdescription"))
                .type(List.of("VerifiableCredential", "SD-document"))
                .issuer("https://catalog.demo.supplytree.org/api/user/catenax")
                .issuanceDate("2022-03-04T07:49:24Z")
                .credentialSubject(DBCredentialSubject.builder()
                        .id("https://catalog.demo.supplytree.org/api/user/holder")
                        .companyNumber("RU-123")
                        .headquarterCountry("RU")
                        .legalCountry("RU")
                        .build())
                .build();
        mongoTemplate.save(vc, sdCollectionName);
        var sdr = GetSelfDescriptionRequest.builder()
                .challenge(RandomStringUtils.random(32, true, true))
                .id("https://catalog.demo.supplytree.org/api/user/holder")
                .build();
        var resp = getMockMvc().perform(post("/selfdescription/by-params")
                        .content(objectMapper.writeValueAsBytes(sdr))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertNotNull(resp.getContentAsString());
        var resVP = VerifiablePresentation.fromJson(resp.getContentAsString());
        Assert.assertNotNull(resVP);
        Assert.assertNotNull(resVP.getVerifiableCredential());
    }
}
