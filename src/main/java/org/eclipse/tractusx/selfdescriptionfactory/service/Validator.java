/********************************************************************************
 * Copyright (c) 2022,2023 T-Systems International GmbH
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.selfdescriptionfactory.service;

import io.vavr.Lazy;
import io.vavr.control.Try;
import jakarta.validation.Valid;
import org.eclipse.tractusx.selfdescriptionfactory.Utils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;
import java.util.function.Supplier;

@Component
@Validated
public class Validator {
    private final Lazy<Validator> self;

    public Validator(@Autowired ObjectFactory<Validator> validatorObjectFactory) {
        self = Lazy.of(validatorObjectFactory::getObject);
    }

    public <R> Supplier<R> validated(Supplier<R> fn) {
        return () -> validated(any -> fn.get()).apply(null);
    }

    public <T, R> Function<T, R> validated(Function<T, R> fn) {
        return t -> fn.andThen(r -> Try.of(() -> self.get().checkIfValid(r))
                .recoverWith(Utils.mapFailure(
                        err -> new ResponseStatusException(HttpStatus.BAD_REQUEST, err.getMessage(), err)
                ))).apply(t).get();
    }

    public <T> T checkIfValid(@Valid T t) {
        return t;
    }
}
