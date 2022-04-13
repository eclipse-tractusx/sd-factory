package net.catenax.selfdescriptionfactory.dto;

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
public class SDDocumentDto {
    String company_number;
    String headquarter_country;
    String legal_country;
    String service_provider;
    String sd_type;
    String bpn;
    String holder;
    String issuer;
}
