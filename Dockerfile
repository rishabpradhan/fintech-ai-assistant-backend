#----build stage------#

FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /build
COPY . .

RUN mvn -B package -pl appilcation -am -DskipTests

#----- Runtime Build-----#

FROM gcr.io/distroless/java25-debian13:nonroot

WORKDIR /app

COPY --from=builder \
     /build/appilcation/target/application-0.0.1-SNAPSHOT.jar \
     application.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/application.jar"]

