package org.eclipse.tractusx.selfdescriptionfactory.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SelfDescriptionSchema
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-10-14T12:35:54.092567+03:00[Europe/Istanbul]")
public class SelfDescriptionSchema {

  @JsonProperty("type")
  private String type;

  @JsonProperty("holder")
  private String holder;

  @JsonProperty("issuer")
  private String issuer;

  public SelfDescriptionSchema type(String type) {
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
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public SelfDescriptionSchema holder(String holder) {
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

  public SelfDescriptionSchema issuer(String issuer) {
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
    SelfDescriptionSchema selfDescriptionSchema = (SelfDescriptionSchema) o;
    return Objects.equals(this.type, selfDescriptionSchema.type) &&
        Objects.equals(this.holder, selfDescriptionSchema.holder) &&
        Objects.equals(this.issuer, selfDescriptionSchema.issuer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, holder, issuer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SelfDescriptionSchema {\n");
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

