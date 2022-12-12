FROM openjdk:11
VOLUME /tmp
ADD ./target/card-service-0.0.1-SNAPSHOT.jar card.jar
ENTRYPOINT ["java","-jar","card.jar"]