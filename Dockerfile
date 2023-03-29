FROM maven:3.8.7-eclipse-temurin-17 AS build

COPY . /sdfactory/

WORKDIR /sdfactory

RUN mvn clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17.0.6_10-jdk-alpine@sha256:02c04793fa49ad5cd193c961403223755f9209a67894622e05438598b32f210e

RUN apk update && apk upgrade

ARG DEPENDENCY=/sdfactory/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

RUN adduser -DH sdfuser && addgroup sdfuser sdfuser

USER sdfuser

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.eclipse.tractusx.selfdescriptionfactory.SelfDescriptionFactoryApplication"]

EXPOSE 8080

HEALTHCHECK CMD curl --fail http://localhost:8080 || exit 1   
