package net.catenax.sdhub.repo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DBVerifiableCredential {
    @JsonProperty("@context")
    private List<String> context;
    private List<String> type;
    private String issuer;
    private String issuanceDate;
    private DBCredentialSubject credentialSubject;
}
