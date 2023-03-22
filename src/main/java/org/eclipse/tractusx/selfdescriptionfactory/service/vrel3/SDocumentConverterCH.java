package org.eclipse.tractusx.selfdescriptionfactory.service.vrel3;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalPersonSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.service.Claims;
import org.eclipse.tractusx.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("gaia-x-ctx")
@RequiredArgsConstructor
public class SDocumentConverterCH implements Converter<SelfdescriptionPostRequest, Claims> {

    private final CustodianWallet custodianWallet;
    @Value("${app.verifiableCredentials.gaia-x-schema}")
    private String gaiaxSchema;
    @Value("${app.verifiableCredentials.catena-x-schema}")
    private String catenaxSchema;

    @Override
    public Claims convert(@NonNull SelfdescriptionPostRequest source) {
        String externalId;
        Map<String, Object> converted;
        if (source instanceof LegalPersonSchema lp) {
            externalId = lp.getExternalId();
            converted = legalPerson(lp);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "LegalPersonSchema is supported only"
            );
        }
        var withExternalId = new HashMap<>(converted);
        withExternalId.put("externalId", externalId);
        return new Claims(
                withExternalId,
                List.of(
                        URI.create(gaiaxSchema),
                        URI.create(catenaxSchema)
                )
        );
    }

    private Map<String, Object> legalPerson(LegalPersonSchema legalPersonSchema) {
        return Map.of(
                "id", custodianWallet.getWalletData(legalPersonSchema.getBpn()).get("did").asText(),
                "type", legalPersonSchema.getType(),
                "ctxsd:bpn", legalPersonSchema.getBpn(),
                "gx-participant:name", custodianWallet.getWalletData(legalPersonSchema.getBpn()).get("name").asText(),
                "gx-participant:registrationNumber", legalPersonSchema.getRegistrationNumber().stream().map(
                                regNum -> Map.of(
                                        "gx-participant:registrationNumberType", regNum.getType(),
                                        "gx-participant:registrationNumberNumber", regNum.getValue()
                                )
                ).toList(),
                "gx-participant:headquarterAddress", Map.of("gx-participant:addressCountryCode", legalPersonSchema.getHeadquarterAddressCountry()),
                "gx-participant:legalAddress", Map.of("gx-participant:addressCountryCode", legalPersonSchema.getLegalAddressCountry())
        );
    }
}
