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

package org.eclipse.tractusx.selfdescriptionfactory;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.vavr.control.Try.failure;

@Slf4j
public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> Function<? super Throwable, ? extends Try<? extends T>>
    mapFailure(Function<? super Throwable, ? extends Throwable> f) {
        return ex -> failure(f.apply(ex));
    }


    public static class TooManyRedirectsException extends Exception{
        public TooManyRedirectsException(){
            super("Got too many redirects");
        }
    }
    public static URLConnection getConnectionIfRedirected(URL url, int tries) throws TooManyRedirectsException, IOException {
        if (tries == 0) throw new TooManyRedirectsException();
        URLConnection connection = url.openConnection();
        String redirect = connection.getHeaderField("Location");
        if (redirect != null){
            connection.getInputStream().close();
            return getConnectionIfRedirected(new URL(redirect), tries - 1);
        } else {
            return connection;
        }
    }

    public static URI uriFromStr(String uriStr) {
        return Try.of(() -> URI.create(uriStr))
                .recoverWith(mapFailure(err -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create an URI from '" + uriStr + "'", err)))
                .get();
    }

    public static <T> Optional<List<T>> getNonEmptyListFromCommaSeparated(String source, Function<String, T> transform) {
        return Optional.of(
                Optional.ofNullable(source)
                        .map(s -> s.split(",")).stream().flatMap(Arrays::stream)
                        .filter(Predicate.not(String::isBlank)).map(String::trim)
                        .map(transform).toList()
        ).filter(Predicate.not(Collection::isEmpty));
    }
}
