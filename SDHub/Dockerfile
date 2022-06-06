#FROM openjdk:17-alpine
FROM openjdk:19-alpine3.16

RUN apk update && apk add --upgrade libsodium && apk add maven && apk add --upgrade maven
  
WORKDIR /sdhub

COPY . /sdhub

RUN mvn clean install -Dmaven.test.skip=true 

WORKDIR /sdhub/target

ENV JDK_HOME /usr/lib/jvm/java-17-openjdk

ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk

ENV PATH $PATH:$JAVA_HOME/bin

RUN mv sd-hub-0.0.1-SNAPSHOT.jar sd-hub.jar 

ENTRYPOINT ["java", "-jar", "sd-hub.jar"]

EXPOSE 8080
