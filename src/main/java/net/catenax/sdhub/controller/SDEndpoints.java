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
public class SDEndpoints {

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
    "id": "http://localhost:8080/selfdescription/vc/a5d1ae5b-ec91-45f3-a145-2d263ab5a676",
    "@context": [
        "https://www.w3.org/2018/credentials/v1",
        "http://atom.outlan.tk/sd-document-v0.1.jsonld"
    ],
    "type": [
        "VerifiableCredential",
        "SD-document"
    ],
    "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
    "issuanceDate": "2022-04-05T21:16:05Z",
    "expirationDate": "2022-07-04T21:16:05Z",
    "credentialSubject": {
        "type": [
            "SD-document"
        ],
        "company_number": "did:web:www.aa.com",
        "headquarter_country": "DE",
        "legal_country": "DE",
        "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
    },
    "proof": {
        "type": "Ed25519Signature2018",
        "created": "2022-04-09T22:19:36Z",
        "proofPurpose": "assertionMethod",
        "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
        "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..eQR2Nxdzuv5oEhJ0Te0buRTyVUjXUxudnY28iIuXipU5_QKsKG3GgQDJpKg1zHVGJW49Ksf1siZf1EmXjtkyCw"
    }
}
    """ )))})
    @GetMapping(value = "/vc/{id}", produces = {"application/vc+ld+json"})
    public VerifiableCredential getSelfDescription(@PathVariable String id) throws Exception {
        return Optional.ofNullable(DBService.getVc(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Verifiable Credential not found"));
    }

    @Operation(
            method = "GET",
            description = "Get a Verifiable Credentials by params"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Verifiable Credentials were found successfully",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
      {
             "@context": [
                 "https://www.w3.org/2018/credentials/v1"
             ],
             "id": "92786175-9dbd-4bbf-9790-e876294e8023",
             "type": [
                 "VerifiablePresentation"
             ],
             "holder": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
             "verifiableCredential": [
                 {
                     "id": "http://localhost:8080/selfdescription/vc/b034c9ab-c099-4a55-a0a9-26113c29dc2f",
             "@context": [
                         "https://www.w3.org/2018/credentials/v1",
                         "http://atom.outlan.tk/sd-document-v0.1.jsonld"
                     ],
                     "type": [
                         "VerifiableCredential",
                         "SD-document"
                     ],
                     "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
                     "issuanceDate": "2022-04-10T22:27:23Z",
                     "expirationDate": "2022-07-09T22:27:23Z",
                     "credentialSubject": {
                         "bpn": "BPNAAAAAA",
                         "company_number": "did:web:www.aa.com",
                         "headquarter_country": "DE",
                         "legal_country": "DE",
                         "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
                     },
                     "proof": {
                         "type": "Ed25519Signature2018",
                         "created": "2022-04-10T22:27:27Z",
                         "proofPurpose": "assertionMethod",
                         "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
                         "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..7vd4Ihv4XfyXBcgMSNke6CidACKue5Xf5HUcCs3t12Jn6ZyHqMD8zs9o79NnPWrixolDgSNOM7o4N7V6Z1o5Bg"
                     }
                 }
             ],
             "proof": {
                 "type": "Ed25519Signature2018",
                 "created": "2022-04-10T22:37:39Z",
                 "proofPurpose": "assertionMethod",
                 "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
                 "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..sBBljNt99ic8iMMj1gg1PmRcCllkh1It0PMcNS0JFgreM2gQeJrPyLya5RBgHiGrm-DS_WYRsGgqCOMb9zgvDQ"
             }
         }
                                    """ )))})
    @GetMapping(value = "/by-params", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids,
            @RequestParam(value = "companyNumbers", required = false) List<String> companyNumbers,
            @RequestParam(value = "headquarterCountries", required = false) List<String> headquarterCountries,
            @RequestParam(value = "legalCountries", required = false) List<String> legalCountries
    ) {
        return DBService.getSelfDescriptions(ids, companyNumbers, headquarterCountries, legalCountries);
    }

    @Operation(
            method = "GET",
            description = "Get a Verifiable Credentials by list of ids"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Verifiable Credentials were found successfully",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
      {
             "@context": [
                 "https://www.w3.org/2018/credentials/v1"
             ],
             "id": "92786175-9dbd-4bbf-9790-e876294e8023",
             "type": [
                 "VerifiablePresentation"
             ],
             "holder": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
             "verifiableCredential": [
                 {
                     "id": "http://localhost:8080/selfdescription/vc/b034c9ab-c099-4a55-a0a9-26113c29dc2f",
             "@context": [
                         "https://www.w3.org/2018/credentials/v1",
                         "http://atom.outlan.tk/sd-document-v0.1.jsonld"
                     ],
                     "type": [
                         "VerifiableCredential",
                         "SD-document"
                     ],
                     "issuer": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh",
                     "issuanceDate": "2022-04-10T22:27:23Z",
                     "expirationDate": "2022-07-09T22:27:23Z",
                     "credentialSubject": {
                         "bpn": "BPNAAAAAA",
                         "company_number": "did:web:www.aa.com",
                         "headquarter_country": "DE",
                         "legal_country": "DE",
                         "id": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh"
                     },
                     "proof": {
                         "type": "Ed25519Signature2018",
                         "created": "2022-04-10T22:27:27Z",
                         "proofPurpose": "assertionMethod",
                         "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
                         "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..7vd4Ihv4XfyXBcgMSNke6CidACKue5Xf5HUcCs3t12Jn6ZyHqMD8zs9o79NnPWrixolDgSNOM7o4N7V6Z1o5Bg"
                     }
                 }
             ],
             "proof": {
                 "type": "Ed25519Signature2018",
                 "created": "2022-04-10T22:37:39Z",
                 "proofPurpose": "assertionMethod",
                 "verificationMethod": "did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh#key-1",
                 "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..sBBljNt99ic8iMMj1gg1PmRcCllkh1It0PMcNS0JFgreM2gQeJrPyLya5RBgHiGrm-DS_WYRsGgqCOMb9zgvDQ"
             }
         }
                                    """ )))})
    @GetMapping(value = "/by-id", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids
    ) {
        return DBService.getSelfDescriptions(ids);
    }

}
