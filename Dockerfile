#FROM openjdk:17 as build
FROM maven:3.8.5-openjdk-18-slim as build

COPY . /sdfactory/

WORKDIR /sdfactory

#RUN microdnf install dos2unix && microdnf clean all
#RUN dos2unix mvnw
#RUN chmod +x mvnw
#RUN dos2unix .mvn/wrapper/maven-wrapper.properties
#RUN ./mvnw clean install -Dmaven.test.skip=true

RUN mvn clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM bellsoft/liberica-openjdk-alpine:17.0.6-10
RUN apk update && apk upgrade
ARG DEPENDENCY=/sdfactory/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
RUN adduser -DH sdfuser && addgroup sdfuser sdfuser
USER sdfuser

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.eclipse.tractusx.selfdescriptionfactory.SelfDescriptionFactoryApplication"]

EXPOSE 8080