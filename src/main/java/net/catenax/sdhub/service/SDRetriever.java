package net.catenax.sdhub.service;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.dto.GetSelfDescriptionRequest;
import net.catenax.sdhub.repo.DBVerifiableCredential;
import net.catenax.sdhub.util.KeystoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SDRetriever {
    private final MongoTemplate mongoTemplate;
    private final SDFactory sdFactory;
    private final ObjectMapper objectMapper;
    private final KeystoreProperties keystoreProperties;
    private final Signer signer;

    @Value("${app.db.sd.collectionName}")
    private String sdCollectionName;

    public VerifiablePresentation getSelfDescriptions(GetSelfDescriptionRequest request) {
        if (StringUtils.isEmpty(request.getChallenge())) {
            throw new RuntimeException("Challenge is empty");
        }
        var query = new Query();
        if (!StringUtils.isEmpty(request.getId())) {
            query = query.addCriteria(Criteria.where("credentialSubject.id").is(request.getId()));
        }
        if (!StringUtils.isEmpty(request.getCompanyNumber())) {
            query = query.addCriteria(Criteria.where("credentialSubject.company_number").is(request.getCompanyNumber()));
        }
        if (!StringUtils.isEmpty(request.getHeadquarterCountry())) {
            query = query.addCriteria(Criteria.where("credentialSubject.headquarter_country").is(request.getHeadquarterCountry()));
        }
        if (!StringUtils.isEmpty(request.getLegalCountry())) {
            query = query.addCriteria(Criteria.where("credentialSubject.legal_country").is(request.getLegalCountry()));
        }

        var res = mongoTemplate.find(query, DBVerifiableCredential.class, sdCollectionName)
                .stream()
                .map(this::convertDBVcToVC)
                .map(it -> {
                    try {
                        return (VerifiableCredential) signer.getSigned(keystoreProperties.getCatenax().getKeyId().iterator().next(), request.getChallenge(), it);
                    } catch (Exception e) {
                        log.error("unable to sign VC", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        try {
            return sdFactory.createVP(res, request.getChallenge());
        } catch (Exception e) {
            throw new RuntimeException("Exception during creating VP", e);
        }
    }

    private VerifiableCredential convertDBVcToVC(DBVerifiableCredential dvc) {
        try {
            return VerifiableCredential.fromJson(objectMapper.writeValueAsString(dvc));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to convert DB VC to VC", e);
        }
    }
}