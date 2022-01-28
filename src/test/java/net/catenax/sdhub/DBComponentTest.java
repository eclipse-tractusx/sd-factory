package net.catenax.sdhub;

import foundation.identity.jsonld.JsonLDObject;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class DBComponentTest {

    String selfDescr = """           
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

    @Autowired
    @Getter
    MockMvc mockMvc;

    @Test
    public void mongoTest() throws Exception {
        var createAction = getMockMvc().perform(post("/selfdescription")
                        .content(selfDescr)
                        .contentType(new MediaType("application", "did+ld+json"))
        );
        createAction.andExpect(MockMvcResultMatchers.status().isOk());
        var getAction = getMockMvc().perform(get("/selfdescription/id/1643307778671")
                .accept(new MediaType("application", "did+ld+json"))
        );
        getAction.andExpect(MockMvcResultMatchers.status().isOk());
        var result = getAction.andReturn().getResponse().getContentAsString();
        var resultLd = JsonLDObject.fromJson(result);

        Assert.assertEquals(JsonLDObject.fromJson(selfDescr), resultLd);
    }
}
