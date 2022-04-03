FROM maven:3-openjdk-11

COPY . /app/
WORKDIR /app/

RUN mvn clean install \
    && mvn help:evaluate -Dexpression=project.version -q -DforceStdout > VERSION \
    && cp rest-api/target/rest-api-$(cat VERSION).jar rest-api.jar

FROM openjdk:11
COPY --from=0 /app/rest-api.jar /app/
WORKDIR /app
CMD ["java", "-jar", "rest-api.jar"]
