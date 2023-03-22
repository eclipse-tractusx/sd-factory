package org.eclipse.tractusx.selfdescriptionfactory.service.vrel3;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.LegalPersonSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.SelfdescriptionPostRequest;
import org.eclipse.tractusx.selfdescriptionfactory.model.vrel3.ServiceOfferingSchema;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
@Profile("gaia-x-ctx")
@RequiredArgsConstructor
public class SDocumentConverterGaiaX extends SDocumentConverter implements Converter<SelfdescriptionPostRequest, Claims> {

    private final CustodianWallet custodianWallet;
    @Value("${app.verifiableCredentials.gaia-x-participant-schema}")
    private String gaiaxParticipantSchema;
    @Value("${app.verifiableCredentials.gaia-x-service-schema}")
    private String gaiaxServiceOfferingSchema;
    @Value("${app.verifiableCredentials.catena-x-schema}")
    private String catenaxSchema;

    @Override
    public Claims convert(@NonNull SelfdescriptionPostRequest source) {
        String externalId;
        Map<String, Object> converted;
        List<URI> contexts;
        if (source instanceof LegalPersonSchema lp) {
            externalId = lp.getExternalId();
            converted = legalPerson(lp);
            contexts = Stream.of(gaiaxParticipantSchema, catenaxSchema).map(URI::create).toList();
        } else if (source instanceof ServiceOfferingSchema so) {
            externalId = so.getExternalId();
            converted = serviceOffering(so);
            contexts = Stream.of(gaiaxServiceOfferingSchema, catenaxSchema).map(URI::create).toList();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "LegalPersonSchema is supported only"
            );
        }
        var withExternalId = new LinkedHashMap<>(converted);
        withExternalId.put("externalId", externalId);
        return new Claims(
                withExternalId,
                contexts
        );
    }

    private Map<String, Object> legalPerson(LegalPersonSchema legalPersonSchema) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", custodianWallet.getWalletData(legalPersonSchema.getBpn()).get("did").asText());
        res.put("type", legalPersonSchema.getType());
        res.put("ctxsd:bpn", legalPersonSchema.getBpn());
        res.put("gx-participant:name", custodianWallet.getWalletData(legalPersonSchema.getBpn()).get("name").asText());
        res.put(
                "gx-participant:registrationNumber",
                legalPersonSchema.getRegistrationNumber().stream().map(
                    regNum -> {
                        var regNumNode = new LinkedHashMap<String, Object>();
                        regNumNode.put("gx-participant:registrationNumberType", regNum.getType().toString());
                        regNumNode.put("gx-participant:registrationNumberNumber", regNum.getValue());
                        return regNumNode;
                    }).toList()
        );
        return res;
    }

    private Map<String, Object> serviceOffering(ServiceOfferingSchema serviceOfferingSchema) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", custodianWallet.getWalletData(serviceOfferingSchema.getHolder()).get("did").asText());
        res.put("type", serviceOfferingSchema.getType());
        res.put("ctxsd:connector-url", "http://connector-placeholder.net");
        res.put("gx-service:providedBy", serviceOfferingSchema.getProvidedBy());
        Map<String, Object> dataAccountExportNode = new LinkedHashMap<>();
        dataAccountExportNode.put("gx-service:requestType", "email");
        dataAccountExportNode.put("gx-service:accessType", "digital");
        dataAccountExportNode.put("gx-service:formatType", "json");
        res.put("gx-service:dataAccountExport", List.of(dataAccountExportNode));
        Optional.of(
                Optional.ofNullable(serviceOfferingSchema.getAggregationOf())
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream)
                        .filter(Predicate.not(String::isBlank)).map(String::trim)
                        .map(Utils::uriFromStr).toList()
        ).filter(Predicate.not(Collection::isEmpty)).ifPresent(aggrOf -> res.put("gx-service:aggregationOf", aggrOf));
        Optional.of(
                Optional.ofNullable(serviceOfferingSchema.getTermsAndConditions())
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream)
                        .filter(Predicate.not(String::isBlank)).map(String::trim)
                        .map(this::getTermsAndConditions)
                        .toList()
        ).filter(Predicate.not(Collection::isEmpty)).ifPresent(termCond -> res.put("gx-service:termsAndConditions", termCond));
        Optional.of(
                Optional.ofNullable(serviceOfferingSchema.getPolicies())
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream)
                        .filter(Predicate.not(String::isBlank)).map(String::trim)
                        .toList()
        ).filter(Predicate.not(Collection::isEmpty)).ifPresent(policies -> res.put("gx-service:policy", policies));
        return res;
    }
}
