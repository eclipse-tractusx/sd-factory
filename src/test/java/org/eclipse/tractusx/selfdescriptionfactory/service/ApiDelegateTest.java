package org.eclipse.tractusx.selfdescriptionfactory.service;


import org.eclipse.tractusx.selfdescriptionfactory.api.vrel3.ApiApiController;
import org.eclipse.tractusx.selfdescriptionfactory.api.vrel3.ApiApiDelegate;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.vrel3.ApiDelegate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiDelegateTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApiApiController apiApiController;

    @MockBean
    private ApiDelegate apiDelegate;


    private SelfdescriptionPostRequest sdr ;

    private  String sdrRequest;

    @BeforeAll
    void setUp()
    {
        MockitoAnnotations.initMocks(this);
        sdrRequest = "{\n" +
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
    }
    @Test
    public void selfdescriptionPostTest() throws Exception {

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.ACCEPTED)).when(apiDelegate).selfdescriptionPost(Mockito.any(SelfdescriptionPostRequest.class));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/rel3/selfdescription")
                .accept(MediaType.APPLICATION_JSON).content(String.valueOf(sdrRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        System.out.println(response.getStatus());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }



}