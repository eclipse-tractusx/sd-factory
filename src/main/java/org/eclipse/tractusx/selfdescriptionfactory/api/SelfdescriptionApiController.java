package org.eclipse.tractusx.selfdescriptionfactory.api;

import org.eclipse.tractusx.selfdescriptionfactory.model.SelfdescriptionPostRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-10-14T12:35:54.092567+03:00[Europe/Istanbul]")
@Controller
public class SelfdescriptionApiController implements SelfdescriptionApi {

    private final SelfdescriptionApiDelegate delegate;

    public SelfdescriptionApiController(@Autowired(required = false) SelfdescriptionApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new SelfdescriptionApiDelegate() {});
    }

    @Override
    public SelfdescriptionApiDelegate getDelegate() {
        return delegate;
    }

}
