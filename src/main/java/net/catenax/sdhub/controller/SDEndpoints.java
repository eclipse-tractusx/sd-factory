package net.catenax.sdhub.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.service.DBService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
