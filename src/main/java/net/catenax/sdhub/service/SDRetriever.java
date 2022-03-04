package net.catenax.sdhub.service;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SDRetriever {
    private final MongoTemplate mongoTemplate;
    private final SDFactory sdFactory;

    @Value("${app.db.sd.collectionName}")
    private String sdCollectionName;

    public VerifiablePresentation getSelfDescriptions(List<String> ids, List<String> companyNumbers,
                                                      List<String> headquarterCountries, List<String> legalCountries,
                                                      String challenge) {
        var query = new Query();
        if (listIsNotEmpty(ids)) {
            query = query.addCriteria(Criteria.where("credentialSubject.id").in(ids));
        }
        if (listIsNotEmpty(companyNumbers)) {
            query = query.addCriteria(Criteria.where("credentialSubject.company_number").in(companyNumbers));
        }
        if (listIsNotEmpty(headquarterCountries)) {
            query = query.addCriteria(Criteria.where("credentialSubject.headquarter_country").in(headquarterCountries));
        }
        if (listIsNotEmpty(legalCountries)) {
            query = query.addCriteria(Criteria.where("credentialSubject.legal_country").in(legalCountries));
        }

        var res = mongoTemplate.find(query, BasicDBObject.class, sdCollectionName)
                .stream()
                .peek(it -> it.remove("_id"))
                .map(it -> VerifiableCredential.fromJson(it.toJson()))
                .collect(Collectors.toList());
        try {
            return sdFactory.createVP(res, challenge);
        } catch (Exception e) {
            throw new RuntimeException("Exception during creating VP", e);
        }
    }

    private boolean listIsNotEmpty(List<?> lst) {
        return lst != null && !lst.isEmpty();
    }
}