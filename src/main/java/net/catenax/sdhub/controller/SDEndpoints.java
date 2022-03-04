package net.catenax.sdhub.controller;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.service.SDRetriever;
import net.catenax.sdhub.service.VerifierService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
@Slf4j
public class SDEndpoints {

    private final VerifierService verifierService;
    private final SDRetriever sdRetriever;

    @PostMapping(consumes = {"application/vp+ld+json"})
    public void publishSelfDescription(@RequestBody VerifiablePresentation verifiablePresentation) throws Exception {
        /*var verifier = verifiableCredentialService.createVerifier(verifiablePresentation);
        if (verifier.verifier().verify(verifiablePresentation)) {
            log.debug("Verifiable Presentation is authentic for controller {}", verifier.controller());
            var vc = verifiablePresentation.getVerifiableCredential();
            verifier = verifiableCredentialService.createVerifier(vc);
            if (verifier.verifier().verify(vc)) {
                log.debug("Verifiable Credential is authentic for controller {}", verifier.controller());
                Document doc = Document.parse(verifiablePresentation.toJson());
                mongoTemplate.save(doc, sdCollectionName);
                return;
            }
        }

         */
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Self-Description is not authentic");
    }

    @GetMapping(value = "/all")
    public VerifiablePresentation getAll() {
        throw new RuntimeException("not implemented");
    }

    @GetMapping(value = "/by-params", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids,
            @RequestParam(value = "companyNumbers", required = false) List<String> companyNumbers,
            @RequestParam(value = "headquarterCountries", required = false) List<String> headquarterCountries,
            @RequestParam(value = "legalCountries", required = false) List<String> legalCountries,
            @RequestParam("challenge") String challenge
    ) {
        return sdRetriever.getSelfDescriptions(ids, companyNumbers, headquarterCountries, legalCountries, challenge);
    }
}
