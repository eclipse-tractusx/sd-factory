package org.eclipse.tractusx.selfdescriptionfactory.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.eclipse.tractusx.selfdescriptionfactory.model.LegalPersonSchema;
import org.eclipse.tractusx.selfdescriptionfactory.model.ServiceOfferingSchema;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;


@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-10-14T12:35:54.092567+03:00[Europe/Istanbul]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LegalPersonSchema.class, name = "LegalPerson"),
        @JsonSubTypes.Type(value = ServiceOfferingSchema.class, name = "ServiceOffering")
})
public interface SelfdescriptionPostRequest {
}
