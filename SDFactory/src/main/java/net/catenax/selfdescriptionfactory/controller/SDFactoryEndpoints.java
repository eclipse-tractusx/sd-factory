package net.catenax.selfdescriptionfactory.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.selfdescriptionfactory.dto.SDDocumentDto;
import net.catenax.selfdescriptionfactory.service.SDFactory;
import net.catenax.selfdescriptionfactory.util.BeanAsMap;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
@Slf4j
public class SDFactoryEndpoints {

    private final SDFactory sdFactory;

    @Operation(
            method = "POST",
            description = "Creates a Verifiable Credential and saves in the DB from the DTO"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Verifiable Credential was created successfully",
                    content = @Content(
                            mediaType = "application/vc+ld+json",
                            examples = @ExampleObject("""
{
    "id": "http://localhost:8080/selfdescription/vc/a5d1ae5b-ec91-45f3-a145-2d263ab5a676",
    "@context": [
        "https://www.w3.org/2018/credentials/v1",
        "https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld"
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
    @PostMapping(consumes = {"application/json"}, produces = {"application/vc+ld+json"})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole(@securityRoles.createRole)")
    public VerifiableCredential publishSelfDescription(@RequestBody SDDocumentDto sdDocumentDto) throws Exception {
        var sdMap = new HashMap<>(BeanAsMap.asMap(sdDocumentDto));
        sdMap.remove("issuer");
        sdMap.remove("holder");
        sdMap.values().removeAll(Collections.singleton(null));
        var objectId = ObjectId.get();
        var verifiedCredentials = sdFactory.createVC(objectId.toHexString(), sdMap,
                sdDocumentDto.getHolder(), sdDocumentDto.getIssuer());
        sdFactory.storeVC(verifiedCredentials, objectId);
        return verifiedCredentials;
    }

    @Operation(
            method = "DELETE",
            description = "Delete a list of Verifiable Credentials from DB"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Verifiable Credentials were deleted successfully")})
    @DeleteMapping
    @PreAuthorize("hasRole(@securityRoles.deleteRole)")
    public void removeSelfDescriptions(@RequestParam(value = "id", required = true) List<String> ids) {
        sdFactory.removeSelfDescriptions(ids);
    }
}
