package net.catenax.sdhub.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.service.DBService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
@Slf4j
public class SDHubEndpoints {

    private final DBService DBService;

    @Operation(
            method = "GET",
            description = "Get a Verifiable Credential by id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Verifiable Credential was found successfully",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
{
  "id": "http://sdhub.int.demo.catena-x.net/selfdescription/vc/62a2327045951b1b8777ac96",
  "@context": [
    "https://www.w3.org/2018/credentials/v1",
    "https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld"
  ],
  "type": [
    "VerifiableCredential",
    "SD-document"
  ],
  "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
  "issuanceDate": "2022-06-09T17:48:32Z",
  "expirationDate": "2022-09-07T17:48:32Z",
  "credentialSubject": {
    "bpn": "BPNL000000000000",
    "company_number": "123456",
    "headquarter_country": "DE",
    "legal_country": "DE",
    "sd_type": "connector",
    "service_provider": "http://test.dot.com",
    "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
  },
  "proof": {
    "type": "Ed25519Signature2018",
    "created": "2022-06-09T17:48:37Z",
    "proofPurpose": "assertionMethod",
    "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
    "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..D4pzxYb8tFIKR22GqAnUxc40PB3gfs5atsv-em6QN_fnF1JwRutnlYiPeg3CFPQORKkFCEiSrInt8feQB0yuDg"
  }
}
""" ))), @ApiResponse(responseCode = "404",
                    description = "Verifiable Credential was not found",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T16:32:22.071+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/selfdescription/by-params"
}
""")))})
    @GetMapping(value = "/vc/{id}", produces = {"application/vc+ld+json"})
    public VerifiableCredential getSelfDescription(@PathVariable String id) {
        return Optional.ofNullable(DBService.getVc(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Verifiable Credential not found"));
    }

    @Operation(
            method = "GET",
            description = "Get a Self-Descriptions by params"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Self-Descriptions were found successfully",
                    content = @Content(
                            mediaType = "application/vp+ld+json",
                            examples = @ExampleObject("""
{
  "@context": [
    "https://www.w3.org/2018/credentials/v1"
  ],
  "id": "1c5bdcbc-9c23-42f9-b7fd-d2d4972e309f",
  "type": [
    "VerifiablePresentation"
  ],
  "holder": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
  "verifiableCredential": [
    {
      "id": "http://sdhub.int.demo.catena-x.net/selfdescription/vc/62a2327045951b1b8777ac96",
      "@context": [
        "https://www.w3.org/2018/credentials/v1",
        "https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld"
      ],
      "type": [
        "VerifiableCredential",
        "SD-document"
      ],
      "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
      "issuanceDate": "2022-06-09T17:48:32Z",
      "expirationDate": "2022-09-07T17:48:32Z",
      "credentialSubject": {
        "bpn": "BPNL000000000000",
        "company_number": "123456",
        "headquarter_country": "DE",
        "legal_country": "DE",
        "sd_type": "connector",
        "service_provider": "http://test.dot.com",
        "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
      },
      "proof": {
        "type": "Ed25519Signature2018",
        "created": "2022-06-09T17:48:37Z",
        "proofPurpose": "assertionMethod",
        "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
        "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..D4pzxYb8tFIKR22GqAnUxc40PB3gfs5atsv-em6QN_fnF1JwRutnlYiPeg3CFPQORKkFCEiSrInt8feQB0yuDg"
      }
    }
  ],
  "proof": {
    "type": "Ed25519Signature2018",
    "created": "2022-06-09T18:04:33Z",
    "proofPurpose": "assertionMethod",
    "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
    "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..tNvU88ecso60jeKE2_HBSq2V9Fy8uGXz0ba0_etq7p8EUzCmYVbJMssNz0UXiuXL-_kWQWOcApHgAGxzMxVRBQ"
  }
}
"""))), @ApiResponse(responseCode = "404",
            description = "Self-Descriptions were not found",
            content = @Content(
                    mediaType = "application/vp+ld+json",
                    examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T16:32:22.071+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/selfdescription/by-params"
}
""")))})
    @GetMapping(value = "/by-params", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids,
            @RequestParam(value = "company_number", required = false) List<String> companyNumbers,
            @RequestParam(value = "headquarter_country", required = false) List<String> headquarterCountries,
            @RequestParam(value = "legal_country", required = false) List<String> legalCountries,
            @RequestParam(value = "service_provider", required = false) List<String> serviceProviders,
            @RequestParam(value = "sd_type", required = false) List<String> sdTypes,
            @RequestParam(value = "bpn", required = false) List<String> bpns
    ) {
        return DBService.getSelfDescriptions(ids, companyNumbers, headquarterCountries, legalCountries, serviceProviders, sdTypes, bpns);
    }

    @Operation(
            method = "GET",
            description = "Get a Self-Descriptions by list of ids"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Self-Descriptions were found successfully",
                    content = @Content(
                            mediaType = "application/vp+ld+json",
                            examples = @ExampleObject("""
{
  "@context": [
    "https://www.w3.org/2018/credentials/v1"
  ],
  "id": "d1cb9b7b-0144-4ca1-aad7-96c9899798bd",
  "type": [
    "VerifiablePresentation"
  ],
  "holder": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
  "verifiableCredential": [
    {
      "id": "http://sdhub.int.demo.catena-x.net/selfdescription/vc/62a2327045951b1b8777ac96",
      "@context": [
        "https://www.w3.org/2018/credentials/v1",
        "https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld"
      ],
      "type": [
        "VerifiableCredential",
        "SD-document"
      ],
      "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
      "issuanceDate": "2022-06-09T17:48:32Z",
      "expirationDate": "2022-09-07T17:48:32Z",
      "credentialSubject": {
        "bpn": "BPNL000000000000",
        "company_number": "123456",
        "headquarter_country": "DE",
        "legal_country": "DE",
        "sd_type": "connector",
        "service_provider": "http://test.dot.com",
        "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
      },
      "proof": {
        "type": "Ed25519Signature2018",
        "created": "2022-06-09T17:48:37Z",
        "proofPurpose": "assertionMethod",
        "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
        "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..D4pzxYb8tFIKR22GqAnUxc40PB3gfs5atsv-em6QN_fnF1JwRutnlYiPeg3CFPQORKkFCEiSrInt8feQB0yuDg"
      }
    }
  ],
  "proof": {
    "type": "Ed25519Signature2018",
    "created": "2022-06-09T18:01:46Z",
    "proofPurpose": "assertionMethod",
    "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
    "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..v0SakzrK2ejNIAZk9vOM2-SRqE-CY3Xgg5dKsXG0UjY9asy0sNA0pS4nL5tFXi-BenYeNdvXEjdzlUtNPlajDA"
  }
}
"""))), @ApiResponse(responseCode = "404",
            description = "Self-Descriptions were not found",
            content = @Content(
                    mediaType = "application/vp+ld+json",
                    examples = @ExampleObject("""
{
  "timestamp": "2022-06-09T16:32:22.071+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/selfdescription/by-params"
}
""")))})
    @GetMapping(value = "/by-id", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids
    ) {
        return DBService.getSelfDescriptions(ids);
    }

}
