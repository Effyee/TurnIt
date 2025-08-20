# --------------------------
# 1. 빌드(Build) 단계
# --------------------------
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /build

# 의존성 캐싱을 위해 build.gradle만 먼저 복사
COPY build.gradle settings.gradle /build/
RUN gradle build --no-daemon -x test

# 전체 소스코드 복사 후 빌드
COPY . .
RUN gradle build --no-daemon -x test

# --------------------------
# 2. 실행(Runtime) 단계
# --------------------------
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /build/build/libs/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
