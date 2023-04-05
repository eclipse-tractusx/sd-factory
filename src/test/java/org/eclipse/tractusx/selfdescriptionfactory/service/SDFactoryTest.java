package org.eclipse.tractusx.selfdescriptionfactory.service;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.clearinghouse.ClearingHouse;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.convert.ConversionService;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SDFactoryTest {

    @MockBean
    private CustodianWallet custodianWallet;

    @SpyBean
    private ConversionService conversionService;

    private String sdRequest;

    @MockBean
    private ClearingHouse clearingHouse;

    @Autowired
    private SDFactory sdFactory;

    private VerifiableCredential vfc;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sdRequest = "{\n" +
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

        vfc = new VerifiableCredential();
        vfc.setDocumentLoader(new DocumentLoader() {
            @Override
            public Document loadDocument(URI uri, DocumentLoaderOptions documentLoaderOptions) throws JsonLdError {
                return null;
            }
        });


    }


    @Test
    public void CreateVcTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Object document = mapper.readValue(sdRequest, SelfdescriptionPostRequest.class);
        Claims pclaim = conversionService.convert(document, Claims.class);
        Mockito.when(conversionService.convert(document, Claims.class)).thenReturn(pclaim);
        var claims = new HashMap<>(pclaim.claims());
        var credentialSubject = CredentialSubject.fromJsonObject(claims);
        var claimsHolder = Optional.of(claims).orElseThrow();
        var verifiableCredential = VerifiableCredential.builder()
                .context(pclaim.vocabulary())
                .issuanceDate(new Date())
                .expirationDate(Date.from(Instant.now().plus(Duration.ofDays(90))))
                .credentialSubject(credentialSubject)
                .build();
        var vc = new VerifiableCredential();
        Mockito.when(custodianWallet.getSignedVC(Mockito.any())).thenReturn(vc);
        Mockito.doNothing().when(clearingHouse).sendToClearingHouse(Mockito.any(), Mockito.any());
        sdFactory.createVC(mapper.readValue(sdRequest, SelfdescriptionPostRequest.class));
        Mockito.verify(clearingHouse, Mockito.times(1)).sendToClearingHouse(Mockito.any(), Mockito.any());
        Mockito.verify(custodianWallet, Mockito.times(1)).getSignedVC(Mockito.any());
        Mockito.verify(conversionService, Mockito.times(3)).convert(Mockito.any(), Mockito.any());
    }


}