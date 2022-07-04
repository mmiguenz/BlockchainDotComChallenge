FROM gradle:6.9.1-jdk8 AS builder
USER root
COPY src src
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
RUN gradle test installDist

FROM openjdk:8u212-jdk-alpine
WORKDIR /home/api
EXPOSE 8080
COPY --from=builder /home/gradle/build/install/BlockChainDotComChallenge  /home/api
RUN chmod +x /home/api/bin/BlockChainDotComChallenge

ENTRYPOINT ["/home/api/bin/BlockChainDotComChallenge"]
