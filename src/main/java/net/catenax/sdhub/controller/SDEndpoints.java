package net.catenax.sdhub.controller;

import com.danubetech.verifiablecredentials.VerifiablePresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.dto.SDDocumentDto;
import net.catenax.sdhub.service.SDFactory;
import net.catenax.sdhub.service.SDRetriever;
import net.catenax.sdhub.util.BeanAsMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("selfdescription")
@RequiredArgsConstructor
@Slf4j
public class SDEndpoints {

    private final SDRetriever sdRetriever;
    private final SDFactory sdFactory;

    @PostMapping(consumes = {"application/json"})
    public void publishSelfDescription(@RequestBody SDDocumentDto sdDocumentDto) throws Exception {
        var sdMap = new HashMap<>(BeanAsMap.asMap(sdDocumentDto));
        sdMap.remove("did");
        var verifiedCredentials = sdFactory.createVC(sdMap, URI.create(sdDocumentDto.getDid()));
        sdFactory.storeVC(verifiedCredentials);
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
