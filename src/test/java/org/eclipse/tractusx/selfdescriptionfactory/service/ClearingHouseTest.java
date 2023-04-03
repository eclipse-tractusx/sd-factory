package org.eclipse.tractusx.selfdescriptionfactory.service;


import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.JsonLDUtils;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouse;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouseRemote;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.convert.ConversionService;

import javax.ws.rs.NotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClearingHouseTest {

    @Value("${app.usersDetails.clearingHouse.uri}")
    private String clearingHouseUrl;

    @Autowired
    protected KeycloakManager keycloakManager;

    @SpyBean
    private ConversionService conversionService;

    @MockBean
    private CustodianWallet custodianWallet;

    @Autowired
    private ClearingHouse clearingHouse;

    VerifiableCredential verifiableCredential ;

    @Autowired
    ObjectMapper objectMapper;

    Object externalId;


    @BeforeAll
    public void setUp() throws JsonProcessingException {
        //Tests
        String sdRequest = "{\n" +
                "  \"externalId\": \"ID01234-123-4321\",\n" +
                "  \"type\": \"LegalPerson\",\n" +
                "  \"holder\": \"BPNL000000000000\",\n" +
                "  \"issuer\": \"CAXSDUMMYCATENAZZ\",\n" +
                "  \"registrationNumber\": [\n" +
                "    {\n" +
                "      \"type\": \"local\",\n" +
                "      \"value\": \"o12345678\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"headquarterAddress.country\": \"DE\",\n" +
                "  \"legalAddress.country\": \"DE\",\n" +
                "  \"bpn\": \"BPNL000000000000\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Object document = mapper.readValue(sdRequest, SelfdescriptionPostRequest.class);

        var claimsHolder = Optional.ofNullable(conversionService.convert(document, Claims.class)).orElseThrow();
        var claims = new HashMap<>(claimsHolder.claims());
        var holder = claims.remove("holder");
        var issuer = claims.remove("issuer");
        var type = claims.get("type");
        externalId = claims.remove("externalId");

        var credentialSubject = CredentialSubject.fromJsonObject(claims);
        verifiableCredential = VerifiableCredential.builder()
                .context(claimsHolder.vocabulary())
                .issuanceDate(new Date())
                .expirationDate(Date.from(Instant.now().plus(Duration.ofDays(90))))
                .credentialSubject(credentialSubject)
                .build();
        JsonLDUtils.jsonLdAdd(verifiableCredential, "issuerIdentifier", issuer);
        JsonLDUtils.jsonLdAdd(verifiableCredential, "holderIdentifier", holder);
        JsonLDUtils.jsonLdAdd(verifiableCredential, "type", type);

    }

    @Test
    public void doWorkTest(){
      //  clearingHouse.doWork(clearingHouseUrl,verifiableCredential,externalId.toString(),"Bearer.shdgsajdjdh");
        ClearingHouseRemote chr = Mockito.mock(ClearingHouseRemote.class,Mockito.CALLS_REAL_METHODS);
     //   Mockito.verify(chr,Mockito.times(1)).doWork(clearingHouseUrl,verifiableCredential,externalId.toString(),"Bearer.shdgsajdjdh");
      //  Mockito.doNothing().when(chr).doWork(clearingHouseUrl,verifiableCredential,externalId.toString(),"Bearer.shdgsajdjdh");
      //  chr.doWork(clearingHouseUrl,verifiableCredential,externalId.toString(),"Bearer.shdgsajdjdh");
     //  Mockito.verify(chr).doWork(clearingHouseUrl,verifiableCredential,externalId.toString(),"Bearer.shdgsajdjdh");

        Mockito.doThrow(new NotFoundException()).when(chr).doWork(clearingHouseUrl,verifiableCredential,externalId.toString(),"Bearer.shdgsajdjdh");
    }




}
