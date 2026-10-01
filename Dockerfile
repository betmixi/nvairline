# ---------- Build stage: bien dich backend + build frontend Angular thanh file jar duy nhat ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cache lop dependency Maven rieng de build lai nhanh hon khi chi sua code
COPY mvnw pom.xml ./
COPY .mvn .mvn

COPY . .
RUN ./mvnw -ntp verify -Pprod -DskipTests -Dmodernizer.skip=true

# ---------- Run stage: chi can JRE + file jar da build, khong can JDK/Node nua ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
