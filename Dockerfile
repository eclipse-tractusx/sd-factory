FROM openjdk:17 as build

COPY . /sdfactory/

WORKDIR /sdfactory

RUN microdnf install dos2unix
RUN dos2unix mvnw
RUN chmod +x mvnw
RUN dos2unix .mvn/wrapper/maven-wrapper.properties

RUN ./mvnw clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

#FROM bellsoft/liberica-openjdk-alpine:17.0.3.1-2
FROM maven:latest

ARG DEPENDENCY=/sdfactory/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

#RUN adduser -DH sdf && addgroup sdf sdf
#USER sdf
RUN groupadd sdf
RUN useradd -g sdf -G sdf
USER sdf

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "org.eclipse.tractusx.selfdescriptionfactory.SelfDescriptionFactoryApplication"]

EXPOSE 8080
