package net.catenax.sdhub.repo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DBCredentialSubject {
    private String id;
    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("headquarter_country")
    private String headquarterCountry;
    @JsonProperty("legal_country")
    private String legalCountry;
}
