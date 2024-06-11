FROM maven as build
WORKDIR /emailsender
COPY pom.xml .
COPY src ./src
RUN mvn -Dmaven.test.skip=true clean package

FROM openjdk:17-alpine
WORKDIR /emailsender
ARG JAR_FILE=/emailsender/target/emailsender*.jar
COPY --from=build ${JAR_FILE} emailsender.jar
ENTRYPOINT ["java", "-jar", "emailsender.jar"]
