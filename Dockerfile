FROM maven:3.8.3-openjdk-17 as BUILD
COPY . /digital-library
RUN mvn install:install-file -Dfile=/digital-library/jdf.jar -DgroupId=cue.lang -DartifactId=word-iterator -Dversion=1.0.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=/digital-library/VnCoreNLP-1.2.jar -DgroupId=vn.pipeline -DartifactId=vncorenlp -Dversion=1.2.0 -Dpackaging=jar
RUN mvn -f /digital-library/pom.xml -B -DskipTests clean package

FROM openjdk:17-jdk
WORKDIR /app
ARG JAR_FILE_NAME=digital-library-0.0.1-SNAPSHOT.jar
COPY --from=BUILD /digital-library/target/${JAR_FILE_NAME} /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]