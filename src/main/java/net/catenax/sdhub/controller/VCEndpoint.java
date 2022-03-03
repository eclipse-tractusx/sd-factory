package net.catenax.sdhub.controller;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.service.VerifierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("vc")
@RequiredArgsConstructor
public class VCEndpoint {

    @Value("${app.wallet.catena-x.did}")
    private String issuerDid;

    private final VerifierService verifierService;

    @GetMapping(produces = {"application/vp+ld+json"})
    public VerifiableCredential getVc(@RequestParam Map<String, String> allRequestParams) throws Exception {
        Map<String, Object> clames =  new HashMap<>(allRequestParams);
        var holderDid = Optional.ofNullable(clames.remove("holderDid")).map(Object::toString).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Holder did is not provided"));
       /* return verifiableCredentialService.createVC(
                clames,
                URI.create(holderDid),
                URI.create(issuerDid)
        );

        */
        return null;
    }

}
