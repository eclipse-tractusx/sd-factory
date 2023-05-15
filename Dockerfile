#################################################################################
# Copyright (c) 2022,2023 T-Systems International GmbH
# Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
################################################################################

FROM maven:3.8.7-eclipse-temurin-17 AS build

COPY . /sdfactory/

WORKDIR /sdfactory

RUN mvn clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17.0.6_10-jdk-alpine

RUN apk update && apk upgrade

ARG DEPENDENCY=/sdfactory/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV USER=sdfuser
ENV UID=1000
ENV GID=1000

RUN addgroup --gid $GID $USER

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --ingroup "$USER" \
    --no-create-home \
    --uid "$UID" \
    "$USER"
    
USER sdfuser

WORKDIR /

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.eclipse.tractusx.selfdescriptionfactory.SelfDescriptionFactoryApplication"]

EXPOSE 8080

HEALTHCHECK CMD curl --fail http://localhost:8080 || exit 1   
