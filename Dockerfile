FROM openjdk:11
WORKDIR /app
COPY target/MinhasContas-0.0.1-SNAPSHOT.jar /app/api.jar
# ENV  URL_DATABASE='jdbc:mysql://54.163.77.151:3306/minhasContas'
ENV  URL_DATABASE='jdbc:mysql://minhascontas.rdrtech.com.br:3306/minhasContas'
ENV SERVER_PORT=8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "api.jar"]

EXPOSE 8081