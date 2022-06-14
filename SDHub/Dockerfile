FROM openjdk:17-alpine as build

COPY . /sdhub/

WORKDIR /sdhub

RUN dos2unix mvnw
RUN chmod +x mvnw
RUN dos2unix .mvn/wrapper/maven-wrapper.properties

RUN ./mvnw clean install -Dmaven.test.skip=true 

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM bellsoft/liberica-openjdk-alpine:17.0.3.1-2

ARG DEPENDENCY=/sdhub/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp",  "app:app/lib/*", "net.catenax.sdhub.SelfDescriptionHubApplication"]

EXPOSE 8080
