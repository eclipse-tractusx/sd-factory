package org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.JsonLDUtils;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouse;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouseRemote;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class ClearingHouseTest {

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

    @MockBean
    Keycloak keycloak;

    Object externalId;


    @BeforeAll
    public void setUp() throws JsonProcessingException {


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
        ClearingHouse chr = Mockito.mock(ClearingHouse.class,Mockito.CALLS_REAL_METHODS);
        chr.keycloakManager = keycloakManager;
        Mockito.doThrow(new NotFoundException()).when(chr).doWork(null,verifiableCredential,externalId.toString(),"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJIVUgzYjZrMzZvbFNQVTRDRTRaMVUxUjhVeHg4eFQwS3p4QXdLb3NkVk1VIn0.eyJleHAiOjE2ODA2MDAyNDcsImlhdCI6MTY4MDU5OTk0NywianRpIjoiNTAwNTJjOGYtMjgyZS00NTM4LWI5YzYtODk0NTY1ZGYzNTY4IiwiaXNzIjoiaHR0cHM6Ly9jZW50cmFsaWRwLmludC5kZW1vLmNhdGVuYS14Lm5ldC9hdXRoL3JlYWxtcy9DWC1DZW50cmFsIiwiYXVkIjpbInJlYWxtLW1hbmFnZW1lbnQiLCJDbDItQ1gtUG9ydGFsIiwiYWNjb3VudCJdLCJzdWIiOiIxOGMzYTZiMy1lY2ZlLTQ1NzItYmJiNC1hZjBjMTgyM2YyMDYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJzYS1jbDItMDIiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1jYXRlbmEteCByZWFsbSIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJtYW5hZ2UtdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJxdWVyeS1jbGllbnRzIl19LCJDbDItQ1gtUG9ydGFsIjp7InJvbGVzIjpbInVwZGF0ZV9hcHBsaWNhdGlvbl9jaGVja2xpc3RfdmFsdWUiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiY2xpZW50SWQiOiJzYS1jbDItMDIiLCJjbGllbnRIb3N0IjoiMTAuMjQwLjAuNiIsInByZWZlcnJlZF91c2VybmFtZSI6InNlcnZpY2UtYWNjb3VudC1zYS1jbDItMDIiLCJjbGllbnRBZGRyZXNzIjoiMTAuMjQwLjAuNiJ9.IzYx5uZlT9qDvls4Lxw6uZXxL3-rMtFq6C8gzylDWZD-A_QD6umdbYJRVEXM--_7qvlHoSac6cDTsYo0XfoojhTuXZhlnnrZON1qyxC3GWEGbG8tqIM7Ns74jIIVhE4lJoB_Q4zNRfVCC1EHfOc7OlEBeJCUSiVeesE1qHpD-gzkHPjIFGPOsXM1aMFo1uAQUbg8oKzwPYkmylXK7LDQ0h8qppoJUKfZs0dP2SoOwJsPROc2tpLJMQcpGxC-Eojxi2slUAWHP6DqR2kaBM2PP7lsISFkvaoGSdDaJvaDpc0Gb69CupnOGwpwdbd3B9Kpe4FzOE1UVDcENyhT1WzQJA");
        chr.sendToClearingHouse(verifiableCredential,externalId.toString());
    }




}