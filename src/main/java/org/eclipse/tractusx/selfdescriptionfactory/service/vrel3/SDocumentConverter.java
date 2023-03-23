/********************************************************************************
 * Copyright (c) 2021,2022 T-Systems International GmbH
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

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
