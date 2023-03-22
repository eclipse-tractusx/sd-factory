package org.eclipse.tractusx.selfdescriptionfactory.service.vrel3;

import io.vavr.control.Try;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.eclipse.tractusx.selfdescriptionfactory.model.v2210.TermsAndConditionsSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URL;

public class SDocumentConverter {
    @Value("${app.maxRedirect:5}")
    private int maxRedirect;

    protected TermsAndConditionsSchema getTermsAndConditions(String urlStr) {
        return Try.of(() -> new URL(urlStr))
                .mapTry(url -> Utils.getConnectionIfRedirected(url, maxRedirect))
                .flatMap(urlConnection -> Try.withResources(urlConnection::getInputStream).of(DigestUtils::sha256Hex))
                .map(sha -> new TermsAndConditionsSchema()
                        .URL(URI.create(urlStr))
                        .hash(sha))
                .recoverWith(Utils.mapFailure(err ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Could not retrieve TermsAndConditions from '" + urlStr + "'",
                                        err)
                        )
                ).get();
    }
}
