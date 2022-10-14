package org.eclipse.tractusx.selfdescriptionfactory.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * LegalPersonSchema
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-10-14T12:35:54.092567+03:00[Europe/Istanbul]")
public class LegalPersonSchema implements SelfdescriptionPostRequest {

  @JsonProperty("registrationNumber")
  private String registrationNumber;

  @JsonProperty("headquarterAddress.country")
  private String headquarterAddressCountry;

  @JsonProperty("legalAddress.country")
  private String legalAddressCountry;

  @JsonProperty("bpn")
  private String bpn;

  @JsonProperty("type")
  private String type;

  @JsonProperty("holder")
  private String holder;

  @JsonProperty("issuer")
  private String issuer;

  public LegalPersonSchema registrationNumber(String registrationNumber) {
    this.registrationNumber = registrationNumber;
    return this;
  }

  /**
   * Get registrationNumber
   * @return registrationNumber
  */
  
  @Schema(name = "registrationNumber", required = false)
  public String getRegistrationNumber() {
    return registrationNumber;
  }

  public void setRegistrationNumber(String registrationNumber) {
    this.registrationNumber = registrationNumber;
  }

  public LegalPersonSchema headquarterAddressCountry(String headquarterAddressCountry) {
    this.headquarterAddressCountry = headquarterAddressCountry;
    return this;
  }

  /**
   * Get headquarterAddressCountry
   * @return headquarterAddressCountry
  */
  
  @Schema(name = "headquarterAddress.country", required = false)
  public String getHeadquarterAddressCountry() {
    return headquarterAddressCountry;
  }

  public void setHeadquarterAddressCountry(String headquarterAddressCountry) {
    this.headquarterAddressCountry = headquarterAddressCountry;
  }

  public LegalPersonSchema legalAddressCountry(String legalAddressCountry) {
    this.legalAddressCountry = legalAddressCountry;
    return this;
  }

  /**
   * Get legalAddressCountry
   * @return legalAddressCountry
  */
  
  @Schema(name = "legalAddress.country", required = false)
  public String getLegalAddressCountry() {
    return legalAddressCountry;
  }

  public void setLegalAddressCountry(String legalAddressCountry) {
    this.legalAddressCountry = legalAddressCountry;
  }

  public LegalPersonSchema bpn(String bpn) {
    this.bpn = bpn;
    return this;
  }

  /**
   * Get bpn
   * @return bpn
  */
  @NotNull 
  @Schema(name = "bpn", required = true)
  public String getBpn() {
    return bpn;
  }

  public void setBpn(String bpn) {
    this.bpn = bpn;
  }

  public LegalPersonSchema type(String type) {
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
    return "LegalPerson";
  }

  public void setType(String type) {
    this.type = type;
  }

  public LegalPersonSchema holder(String holder) {
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

  public LegalPersonSchema issuer(String issuer) {
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
    LegalPersonSchema legalPersonSchema = (LegalPersonSchema) o;
    return Objects.equals(this.registrationNumber, legalPersonSchema.registrationNumber) &&
        Objects.equals(this.headquarterAddressCountry, legalPersonSchema.headquarterAddressCountry) &&
        Objects.equals(this.legalAddressCountry, legalPersonSchema.legalAddressCountry) &&
        Objects.equals(this.bpn, legalPersonSchema.bpn) &&
        Objects.equals(this.type, legalPersonSchema.type) &&
        Objects.equals(this.holder, legalPersonSchema.holder) &&
        Objects.equals(this.issuer, legalPersonSchema.issuer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registrationNumber, headquarterAddressCountry, legalAddressCountry, bpn, type, holder, issuer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LegalPersonSchema {\n");
    sb.append("    registrationNumber: ").append(toIndentedString(registrationNumber)).append("\n");
    sb.append("    headquarterAddressCountry: ").append(toIndentedString(headquarterAddressCountry)).append("\n");
    sb.append("    legalAddressCountry: ").append(toIndentedString(legalAddressCountry)).append("\n");
    sb.append("    bpn: ").append(toIndentedString(bpn)).append("\n");
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

