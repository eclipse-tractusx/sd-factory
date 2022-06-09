package net.catenax.selfdescriptionfactory;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import net.catenax.selfdescriptionfactory.dto.SDDocumentDto;
import net.catenax.selfdescriptionfactory.service.SDFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DirtiesContext
public class DatabaseTest {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SDFactory sdFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${app.db.sd.collectionName}")
    String sdCollectionName;
    @Value("${app.verifiableCredentials.holder}")
    String holder;
    @Value("${app.verifiableCredentials.issuer}")
    String issuer;

    @Test
    @WithMockUser(username = "test", roles = "add_self_descriptions")
    public void testDB() throws Exception {
        var sdDTO = SDDocumentDto.builder()
                .issuer(issuer)
                .holder(holder)
                .company_number("DE-123")
                .headquarter_country("DE")
                .legal_country("DE")
                .bpn("BPN123")
                .build();

        var vcResp = mockMvc.perform(post("/selfdescription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sdDTO))).andExpect(status().isCreated()).andReturn().getResponse();
        var respStr = vcResp.getContentAsString();
        Assert.assertNotNull(respStr);
        var resVC = VerifiableCredential.fromJson(respStr);
        var dbJson = mongoTemplate.findAll(DBObject.class, sdCollectionName).iterator().next();
        dbJson.removeField("_id");
        var dbVc = VerifiableCredential.fromJson(dbJson.toString());
        Assert.assertEquals(resVC, dbVc);
    }

}
