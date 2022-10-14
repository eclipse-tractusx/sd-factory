package org.eclipse.tractusx.selfdescriptionfactory.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.net.URI;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ServiceOfferingSchema
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-10-14T12:35:54.092567+03:00[Europe/Istanbul]")
public class ServiceOfferingSchema implements SelfdescriptionPostRequest {

  @JsonProperty("providedBy")
  private URI providedBy;

  @JsonProperty("aggregationOf")
  private String aggregationOf;

  @JsonProperty("termsAndConditions")
  private String termsAndConditions;

  @JsonProperty("policies")
  private String policies;

  @JsonProperty("type")
  private String type;

  @JsonProperty("holder")
  private String holder;

  @JsonProperty("issuer")
  private String issuer;

  public ServiceOfferingSchema providedBy(URI providedBy) {
    this.providedBy = providedBy;
    return this;
  }

  /**
   * Get providedBy
   * @return providedBy
  */
  @NotNull @Valid 
  @Schema(name = "providedBy", required = true)
  public URI getProvidedBy() {
    return providedBy;
  }

  public void setProvidedBy(URI providedBy) {
    this.providedBy = providedBy;
  }

  public ServiceOfferingSchema aggregationOf(String aggregationOf) {
    this.aggregationOf = aggregationOf;
    return this;
  }

  /**
   * Get aggregationOf
   * @return aggregationOf
  */
  
  @Schema(name = "aggregationOf", required = false)
  public String getAggregationOf() {
    return aggregationOf;
  }

  public void setAggregationOf(String aggregationOf) {
    this.aggregationOf = aggregationOf;
  }

  public ServiceOfferingSchema termsAndConditions(String termsAndConditions) {
    this.termsAndConditions = termsAndConditions;
    return this;
  }

  /**
   * Get termsAndConditions
   * @return termsAndConditions
  */
  
  @Schema(name = "termsAndConditions", required = false)
  public String getTermsAndConditions() {
    return termsAndConditions;
  }

  public void setTermsAndConditions(String termsAndConditions) {
    this.termsAndConditions = termsAndConditions;
  }

  public ServiceOfferingSchema policies(String policies) {
    this.policies = policies;
    return this;
  }

  /**
   * Get policies
   * @return policies
  */
  
  @Schema(name = "policies", required = false)
  public String getPolicies() {
    return policies;
  }

  public void setPolicies(String policies) {
    this.policies = policies;
  }

  public ServiceOfferingSchema type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @NotNull 
  @Schema(name = "type", required = true)
  public String getType() {
    return "ServiceOffering";
  }

  public void setType(String type) {
    this.type = type;
  }

  public ServiceOfferingSchema holder(String holder) {
    this.holder = holder;
    return this;
  }

  /**
   * Get holder
   * @return holder
  */
  @NotNull 
  @Schema(name = "holder", required = true)
  public String getHolder() {
    return holder;
  }

  public void setHolder(String holder) {
    this.holder = holder;
  }

  public ServiceOfferingSchema issuer(String issuer) {
    this.issuer = issuer;
    return this;
  }

  /**
   * Get issuer
   * @return issuer
  */
  @NotNull 
  @Schema(name = "issuer", required = true)
  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceOfferingSchema serviceOfferingSchema = (ServiceOfferingSchema) o;
    return Objects.equals(this.providedBy, serviceOfferingSchema.providedBy) &&
        Objects.equals(this.aggregationOf, serviceOfferingSchema.aggregationOf) &&
        Objects.equals(this.termsAndConditions, serviceOfferingSchema.termsAndConditions) &&
        Objects.equals(this.policies, serviceOfferingSchema.policies) &&
        Objects.equals(this.type, serviceOfferingSchema.type) &&
        Objects.equals(this.holder, serviceOfferingSchema.holder) &&
        Objects.equals(this.issuer, serviceOfferingSchema.issuer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(providedBy, aggregationOf, termsAndConditions, policies, type, holder, issuer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceOfferingSchema {\n");
    sb.append("    providedBy: ").append(toIndentedString(providedBy)).append("\n");
    sb.append("    aggregationOf: ").append(toIndentedString(aggregationOf)).append("\n");
    sb.append("    termsAndConditions: ").append(toIndentedString(termsAndConditions)).append("\n");
    sb.append("    policies: ").append(toIndentedString(policies)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    holder: ").append(toIndentedString(holder)).append("\n");
    sb.append("    issuer: ").append(toIndentedString(issuer)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

