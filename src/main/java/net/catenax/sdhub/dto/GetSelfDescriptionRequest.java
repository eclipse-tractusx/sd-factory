package net.catenax.sdhub.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetSelfDescriptionRequest {
    private String id;
    private String companyNumber;
    private String headquarterCountry;
    private String legalCountry;
    private String challenge;
}
