package net.catenax.sdhub;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.mongodb.DBObject;
import net.catenax.sdhub.service.VerifiableCredentialService;
import org.bson.Document;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
class DatabaseTests {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private VerifiableCredentialService verifiableCredentialService;

	@Test
	void testDB() throws Exception {
		Map<String, Object> claims = new LinkedHashMap<>();
		claims.put("company", "My cool company");
		claims.put("operator", "My operator");
		claims.put("region", "Germany");
		claims.put("service", "Connector");
		VerifiableCredential vc = verifiableCredentialService.createVC(
				claims,
				URI.create("https://catalog.demo.supplytree.org/api/user/holder"),
				URI.create("https://catalog.demo.supplytree.org/api/user/sd-hub")
		);
		Document doc = Document.parse(vc.toJson());
		mongoTemplate.save(doc, "collection");
		List<DBObject> all = mongoTemplate.findAll(DBObject.class, "collection");
		DBObject one = all.get(0);
		one.removeField("_id");
		System.out.println(one);
		vc = VerifiableCredential.fromJson(one.toString());
		Assert.assertTrue(verifiableCredentialService.verifySDHubVC(vc));
	}

}
