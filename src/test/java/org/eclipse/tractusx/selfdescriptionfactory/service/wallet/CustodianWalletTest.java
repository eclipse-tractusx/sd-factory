package org.eclipse.tractusx.selfdescriptionfactory.service.wallet;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import foundation.identity.jsonld.JsonLDUtils;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.eclipse.tractusx.selfdescriptionfactory.service.KeycloakManager;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.admin.client.Config;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.SSLContext;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustodianWalletTest {
    @Value("${app.usersDetails.clearingHouse.uri}")
    private String clearingHouseUrl;

    @MockBean
    protected KeycloakManager keycloakManager;

    @SpyBean
    private ConversionService conversionService;
    @Autowired
    CustodianWallet custodianWallet;
    @MockBean
    Keycloak keycloak;
    VerifiableCredential verifiableCredential;
    Object externalId;
    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient webClient;
    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    WebClient.ResponseSpec responseSpec;

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
    public void custodianWalletTest() {

        MockedStatic<WebClient> webClientStatic = Mockito.mockStatic(WebClient.class);
        webClientStatic.when(() -> WebClient.create(anyString())).thenReturn(webClient);
        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.accept(MediaType.ALL)).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        TokenManager tkm = Mockito.mock(TokenManager.class);

        Keycloak klk = Keycloak.getInstance("https://centralidp.int.demo.catena-x.net/auth", "CX-Central", "sa-cl5-custodian-1", null, "sa-cl5-custodian-1", "iim6OwFYD10QsCueq4EEK5VaQ3cLOzaA", null, null);
        Mockito.when(keycloakManager.getKeycloack(any())).thenReturn(keycloak);
        Mockito.when(keycloak.tokenManager()).thenReturn(klk.tokenManager());
        Mockito.when(tkm.getAccessTokenString()).thenReturn("Bearer.jakdhaksbdabdadb");
        CustodianWallet cw = Mockito.mock(CustodianWallet.class);
        cw.getSignedVC(verifiableCredential);


    }
}