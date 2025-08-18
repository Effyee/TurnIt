# 1. 빌드(Build) 단계: Gradle을 사용하여 Jar 파일 생성
FROM gradle:8.5-jdk17-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /build

# build.gradle 파일 먼저 복사 (의존성 캐싱을 위함)
COPY build.gradle settings.gradle /build/
# 의존성 다운로드
RUN gradle build --no-daemon -x test

# 전체 소스코드 복사
COPY . .
# 애플리케이션 빌드 (테스트는 제외)
RUN gradle build --no-daemon -x test

# -----------------------------------------------

# 2. 실행(Runtime) 단계: 실제 서버에서 사용할 최종 이미지 생성
FROM amazoncorretto:17-alpine-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 Jar 파일을 실행 단계 이미지로 복사
COPY --from=builder /build/build/libs/*.jar ./app.jar

# 컨테이너가 시작될 때 실행할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]