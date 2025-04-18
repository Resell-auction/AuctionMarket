# 베이스 이미지 지정 (JDK 포함)
# 단일 스테이지 빌드: 빌드 환경과 실행 환경을 분리하지 않음
FROM eclipse-temurin:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 전체 복사
# 레이어 캐싱을 효과적으로 활용하지 않고 모든 파일을 한 번에 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle Wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 프로젝트 빌드 (테스트 포함, Gradle 데몬 사용 - 기본값)
# '--no-daemon' 및 '-x test' 옵션 제거
# 빌드 시 테스트 코드를 실행하고, Gradle 데몬을 사용하여 빌드 (로컬 환경에서는 빠를 수 있으나 CI 환경에서는 비효율적일 수 있음)
RUN ./gradlew build

# 메타 데이터 라벨 (선택 사항)
LABEL org.opencontainers.image.authors="FinalProject8" \
      org.opencontainers.image.title="AuctionMarketApp-NonOptimized" \
      org.opencontainers.image.source="https://github.com/FinalProject8/AuctionMarket"

# 빌드된 JAR 파일 경로 지정 (경로는 실제 생성되는 위치에 따라 다를 수 있음)
# WORKDIR이 /app 이므로 /app/build/libs/*.jar 가 됨
# ENTRYPOINT에서 직접 경로를 지정하므로 별도 COPY는 필요 없을 수 있으나, 명시적으로 JAR 파일 위치를 확인하기 위해 추가 가능
# COPY build/libs/*.jar app.jar

# 기본 포트
EXPOSE 8080

# 컨테이너 시작 시 실행 명령어
# JAR 파일 경로를 직접 지정
ENTRYPOINT ["java", "-jar", "/app/build/libs/*.jar"]

# 참고: 위 ENTRYPOINT의 "*.jar" 부분은 실제 생성되는 JAR 파일 이름으로 정확히 지정하는 것이 좋습니다.
# 예를 들어, 빌드 결과가 'auction-market-0.0.1-SNAPSHOT.jar' 라면 다음과 같이 수정합니다.
# ENTRYPOINT ["java", "-jar", "/app/build/libs/auction-market-0.0.1-SNAPSHOT.jar"]
