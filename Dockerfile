FROM maven:3.8.3-openjdk-17 as BUILD

WORKDIR /digital-library

COPY src/ ./src
COPY pom.xml ./
COPY models/ ./models
COPY jdf.jar ./
COPY VnCoreNLP-1.2.jar ./

RUN mvn -f /digital-library/pom.xml -B -DskipTests clean install

FROM openjdk:17-jdk

WORKDIR /app

ARG JAR_FILE_NAME=digital-library-0.0.1-SNAPSHOT.jar

COPY --from=BUILD /digital-library/target/${JAR_FILE_NAME} ./app.jar
COPY --from=BUILD /digital-library/src/main/resources ./src/main/resources
COPY --from=BUILD /digital-library/jdf.jar ./
COPY --from=BUILD /digital-library/VnCoreNLP-1.2.jar ./

EXPOSE 8080
CMD ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar /app/app.jar"]
