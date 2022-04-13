package net.catenax.sdhub.service;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A query interface implementation of SD-hub
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DBService {
    private final MongoTemplate mongoTemplate;
    private final SDHub sdHub;

    @Value("${app.db.sd.collectionName}")
    private String sdCollectionName;

    /**
     * Searches the VerifiableCredentials by the parameter to include them to the VerifiablePresentation
     *
     * @param ids                  Holder identities
     * @param companyNumbers       query parameter
     * @param headquarterCountries query parameter
     * @param legalCountries       query parameter
     * @param bpns                 query parameter
     * @return Verifiable Presentation
     */
    public VerifiablePresentation getSelfDescriptions(List<String> ids, List<String> companyNumbers,
                                                      List<String> headquarterCountries, List<String> legalCountries,
                                                      List<String> serviceProviders, List<String> sdTypes,
                                                      List<String> bpns) {
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
        if (listIsNotEmpty(serviceProviders)) {
            query = query.addCriteria(Criteria.where("credentialSubject.service_provider").in(serviceProviders));
        }
        if (listIsNotEmpty(legalCountries)) {
            query = query.addCriteria(Criteria.where("credentialSubject.sd_type").in(sdTypes));
        }
        if (listIsNotEmpty(bpns)) {
            query = query.addCriteria(Criteria.where("credentialSubject.bpn").in(bpns));
        }
        return retriveVp(query);
    }

    /**
     * Searches the VerifiableCredentials by the parameter to include them to the VerifiablePresentation
     *
     * @param ids VC identities
     * @return Verifiable Presentation
     */
    public VerifiablePresentation getSelfDescriptions(List<String> ids) {
        var query = new Query();
        if (listIsNotEmpty(ids)) {
            query = query.addCriteria(Criteria.where("id").in(ids));
        }
        return retriveVp(query);
    }

    private VerifiablePresentation retriveVp(Query query) {
        var res = mongoTemplate.find(query, Document.class, sdCollectionName)
                .stream()
                .peek(it -> it.remove("_id"))
                .map(it -> VerifiableCredential.fromJson(it.toJson()))
                .collect(Collectors.toList());
        try {
            return sdHub.createVP(res);
        } catch (Exception e) {
            throw new RuntimeException("Exception during creating VP", e);
        }
    }

    private boolean listIsNotEmpty(List<?> lst) {
        return lst != null && !lst.isEmpty();
    }

    /**
     * Searches the VerifiableCredential by the id
     *
     * @param id VC id
     * @return Verifiable Presentation
     */
    public VerifiableCredential getVc(String id) {
        var query = Query.query(
                new Criteria().orOperator(
                        Criteria.where("id").is(id),
                        Criteria.where("id").is(ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/selfdescription/vc/" + id).build().toString())
                )
        );
        var document = mongoTemplate.findOne(query, Document.class, sdCollectionName);
        if (Objects.nonNull(document)) {
            document.remove("_id");
            return VerifiableCredential.fromJson(document.toJson());
        } else {
            return null;
        }
    }
}