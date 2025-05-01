# 베이스 이미지 지정
FROM eclipse-temurin:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 전체 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle Wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 프로젝트 빌드
RUN ./gradlew build

# 메타 데이터 라벨
LABEL org.opencontainers.image.authors="FinalProject8" \
      org.opencontainers.image.title="AuctionMarketApp-NonOptimized" \
      org.opencontainers.image.source="https://github.com/FinalProject8/AuctionMarket"


# 기본 포트
EXPOSE 8080

# 컨테이너 시작 시 실행 명령어
# JAR 파일 경로를 직접 지정
ENTRYPOINT ["java", "-jar", "/app/build/libs/*.jar"]