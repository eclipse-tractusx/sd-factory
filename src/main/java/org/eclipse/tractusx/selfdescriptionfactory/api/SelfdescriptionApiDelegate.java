package org.eclipse.tractusx.selfdescriptionfactory.api;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import org.eclipse.tractusx.selfdescriptionfactory.model.SelfdescriptionPostRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link SelfdescriptionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-10-14T12:35:54.092567+03:00[Europe/Istanbul]")
public interface SelfdescriptionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /selfdescription : Creates a Verifiable Credential and returns it
     *
     * @param selfdescriptionPostRequest parameters to generate VC (required)
     * @return Created (status code 201)
     * @see SelfdescriptionApi#selfdescriptionPost
     */
    default ResponseEntity<VerifiableCredential> selfdescriptionPost(SelfdescriptionPostRequest selfdescriptionPostRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
