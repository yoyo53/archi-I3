FROM openjdk:17-alpine AS base

WORKDIR /app

FROM base AS build

COPY . .

RUN ./gradlew build

FROM base AS final

COPY --from=build /app/build/libs/*-SNAPSHOT.jar main.jar

CMD ["java", "-jar", "main.jar"]