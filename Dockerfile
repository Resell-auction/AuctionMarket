# 빌드 => 소스 코드 컴파일 + JAR 파일 생성

# 베이스 이미지 지정
# as builder: SQL문 별명 정하는 것처럼 별칭 정함 => 나중에 이 빌드 과정을 참조할 수 있게함 => 멀티 스테이지 빌드
FROM eclipse-temurin:17-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 복사 => 호스트 머신에 Maven 설치 없이 프로젝트의 Gradle Wrapper 를 사용하여 빌드
# => Maven Wrapper 로 사용할 거면 따로 변경 해줘야됨
# src보다 먼저 복사하여 의존성 변경 시에만 이 레이어가 재빌드되도록 설정 => 레이어 캐싱 활용
COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

# 의존성 파일 복사 + 다운로드
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src ./src

# 프로젝트 빌드 + 패키징 => JAR 파일 생성
# daemon: Gradle에서 빌드 속도를 높히기 위해 사용하는 백그라운드 프로세스 (사용이 기본값)
# => 그런데 어차피 CI/Docker 빌드는 대부분 단발성이기에 프로세스를 끔
# => 빌드 자체는 자주 일어나나 (push 할때 마다 빌드함), 매번 새로운 가상 머신에서 빌드하기 때문에 단발성 빌드임
#  -x test 붙이면 테스트코드 검증은 안함,  -x test 안붙이면 빌드하고 테스트 코드까지 싹 돌려줌
# => 그런데 빌드 때, 테스트코드 안돌리고 나중에 CI 워크플로우에서 돌려도 됨
RUN #./gradlew build --no-daemon
RUN ./gradlew build -x test --no-daemon


# 실행 => 빌드된 애플리케이션 실행을 위한 환경 구성
# JRE만 포함된 경량 이미지를 사용하여 최종 이미지 크기를 최소화
FROM eclipse-temurin:17-jdk-alpine

# 메타 데이터 라벨
LABEL org.opencontainers.image.authors="FinalProject8" \
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