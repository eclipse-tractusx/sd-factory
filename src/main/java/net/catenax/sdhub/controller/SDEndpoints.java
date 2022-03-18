package net.catenax.sdhub.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.dto.SDDocumentDto;
import net.catenax.sdhub.service.DBService;
import net.catenax.sdhub.service.SDFactory;
import net.catenax.sdhub.util.BeanAsMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
@Slf4j
public class SDEndpoints {

    private final DBService DBService;
    private final SDFactory sdFactory;

    @PostMapping(consumes = {"application/json"}, produces = {"application/vc+ld+json"})
    @ResponseStatus(HttpStatus.CREATED)
    public VerifiableCredential publishSelfDescription(@RequestBody SDDocumentDto sdDocumentDto) throws Exception {
        var sdMap = new HashMap<>(BeanAsMap.asMap(sdDocumentDto));
        sdMap.remove("did");
        var verifiedCredentials = sdFactory.createVC(sdMap, URI.create(sdDocumentDto.getDid()));
        sdFactory.storeVC(verifiedCredentials);
        return verifiedCredentials;
    }

    @PostMapping(value = "/vc", consumes = {"application/vc+ld+json"})
    public void publishSelfDescription(@RequestBody VerifiableCredential verifiableCredential) throws Exception {
        sdFactory.storeVCWithCheck(verifiableCredential);
    }

    @GetMapping(value = "/vc/{id}", produces = {"application/vc+ld+json"})
    public VerifiableCredential getSelfDescription(@PathVariable String id) throws Exception {
        return Optional.ofNullable(DBService.getVc(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Verifiable Credential not found"));
    }

    @GetMapping(value = "/by-params", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids,
            @RequestParam(value = "companyNumbers", required = false) List<String> companyNumbers,
            @RequestParam(value = "headquarterCountries", required = false) List<String> headquarterCountries,
            @RequestParam(value = "legalCountries", required = false) List<String> legalCountries,
            @RequestParam("challenge") String challenge
    ) {
        return DBService.getSelfDescriptions(ids, companyNumbers, headquarterCountries, legalCountries, challenge);
    }

    @GetMapping(value = "/by-id", produces = {"application/vp+ld+json"})
    public VerifiablePresentation getSelfDescriptions(
            @RequestParam(value = "id", required = false) List<String> ids,
            @RequestParam("challenge") String challenge
    ) {
        return DBService.getSelfDescriptions(ids, challenge);
    }

    @DeleteMapping(value = "/by-id")
    public void removeSelfDescriptions(@RequestParam(value = "id", required = true) List<String> ids) {
        DBService.removeSelfDescriptions(ids);
    }

}
