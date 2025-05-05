FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

COPY src ./src

RUN #./gradlew build --no-daemon
RUN ./gradlew build -x test --no-daemon



FROM eclipse-temurin:17-jre-alpine
# JRE만 포함된 경량 이미지를 사용하여 최종 이미지 크기를 최소화

# 메타 데이터 라벨
LABEL org.opencontainers.image.authors="Resell-auction" \
      org.opencontainers.image.title="AuctionMarketApp" \
      org.opencontainers.image.source="https://github.com/FinalProject8/AuctionMarket"

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물 복사
# --from=builder: 위에서 as builder 한 것을 참조
COPY --from=builder /app/build/libs/*.jar app.jar

# 기본 포트
EXPOSE 8080

# 컨테이너 시작 시 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]