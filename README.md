# 🛍 C2C 경매 서비스

<p align="center">
  <img src="https://github.com/user-attachments/assets/6ff80ae8-5810-4b33-83de-b2aa207d46e3">
</p>
<br>

## 🙋‍♂️팀원 소개
<div align="center">
<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/184fcc50-d6b3-4d9e-8b43-2bb0406f2f80" width="200" height="200"><br>
      <div><b>팀장</b></div>
      <div><a href="https://github.com/lh991117">이한빈</a></div>
      <div>경매</div>
      <div>Redis와 Caffeine 성능 비교</div>
      <div>검색 기능 강화</div>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/692bd79e-2d9b-4faa-824a-70547c8b48a6" width="200" height="200"><br>
      <div><b>부팀장</b></div>
      <div><a href="https://github.com/Seung-min-88">이승민</a></div>
      <div>결제</div>
      <div>WebSocket 실시간 경매</div>
      <div>스케줄링 서버</div>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/b6d4c66c-17f3-403b-a7fe-367bb6c7140e" width="200" height="200"><br>
      <div><b>팀원</b></div>
      <div><a href="https://github.com/pathfinder357">최유준</a></div>
      <div>애플리케이션 준비 및 컨테이너화</div>
      <div> Terraform 코드 작성 (IaC)</div>
      <div>배포 환경 검증 및 트러블슈팅</div>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/5dddceb2-e3c2-451e-99fe-a8783e3ce9f9" width="200" height="200"><br>
      <div><b>팀원</b></div>
      <div><a href="https://github.com/uyr83157">정의용</a></div>
      <div>BigQuery + GA4 활용한 통계 API</div>
      <div>BigQuery ETL 파이프라인 구축</div>
      <div>ELK 스택 로깅 + WAF 도입</div>
      <div>메트릭 모니터링</div>
      <div>Docker image 최적화</div>
      <td align="center">
      <img src="https://github.com/user-attachments/assets/d7487536-590f-45f5-93e1-e26300690192" width="200" height="200"><br>
      <div><b>팀원</b></div>
      <div><a href="https://github.com/pathfinder357">송윤정</a></div>
      <div>쿠폰</div>
      <div>AWS EventBridge</div>
      <div>RestDocs API 자동화</div>
      <div>분산락을 이용한 쿠폰 대량 발급</div>
        <td align="center">
      <img src="https://github.com/user-attachments/assets/4bf16cb2-efd6-48fd-9d45-1d27a518fc85" width="200" height="200"><br>
      <div><b>팀원</b></div>
      <div><a href="https://github.com/pathfinder357">박현승</a></div>
      <div>물품</div>
      <div>물품 이미지 업로드</div>
      <div>AWS S3 기반 이미지 저장 구조 구현</div>
      <div>CloudFront 연동으로 로딩 속도 개선</div>
  </tr>
</table>
</div>

<br>

## 📄프로젝트 소개
**개발 기간**: 2025.04.01 ~ 2025.05.06

`C2C 경매 서비스`는 사용자가 물품을 경매에 올리고 사용자가 실시간으로 물품을 경매할 수 있는 C2C 경매 사이트입니다.
<br>

## 🛠 기술 스택 
**라이브러리 & 프레임워크** <br>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> 
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> <br><br>
**데이터 베이스 & 캐싱** <br> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/elasticsearch-%230377CC.svg?style=for-the-badge&logo=elasticsearch&logoColor=white"> <img src="https://img.shields.io/badge/opensearch-005EB8?style=for-the-badge&logo=opensearch&logoColor=white"> <br><br>
**클라우드 & 인프라** <br>
<img src="https://img.shields.io/badge/Amazon%20S3-FF9900?style=for-the-badge&logo=amazons3&logoColor=white"> <img src="https://img.shields.io/badge/terraform-%235835CC.svg?style=for-the-badge&logo=terraform&logoColor=white">
<img src="https://img.shields.io/badge/amazon%20ec2-FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/amazon%20ecs-FF9900.svg?style=for-the-badge&logo=amazonecs&logoColor=white">
<img src="https://img.shields.io/badge/aws%20lambda-FF9900.svg?style=for-the-badge&logo=awslambda&logoColor=white"><br><br>
**데이터 트래킹** <br>
<img src="https://img.shields.io/badge/google%20analytics-E37400.svg?style=for-the-badge&logo=googleanalytics&logoColor=white"><br><br>
**스토리지** <br>
<img src="https://img.shields.io/badge/google%20cloud%20storage-AECBFA.svg?style=for-the-badge&logo=googlecloudstorage&logoColor=white"><br><br>
**테스트 & 모니터링** <br>
<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white"> <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white">
<img src="https://img.shields.io/badge/grafana-%23F46800.svg?style=for-the-badge&logo=grafana&logoColor=white"> <img src="https://img.shields.io/badge/kibana-005571.svg?style=for-the-badge&logo=kibana&logoColor=white"><br><br>
**협업 및 문서화 도구** <br>
<img src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white"> <br><br>
**컨테이너 & 배포** <br>
<img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white">
<img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white"> <br><br>

## ⚙️ System Architecture

<p align="center">
  <img src="https://github.com/user-attachments/assets/ef1e0408-423e-4962-9067-0ab77ee5460f">
</p>

<br>

## ⛓️ ERD

<p align="center">
  <img src="img/erd2.png" width="700" height="600">
</p>

<br>

## 🧱[와이어프레임](https://docs.google.com/presentation/d/1J85rLEqN8q-g5gu4F7oyU-kvXNy68qDt/edit#slide=id.p6)
<br>

## 📰[API 명세서](https://www.notion.so/API-1e73dcf2500780479a9dd06e715e0f33?pvs=4)
<br>

[RestDocs API](http://auction-market-restdocs-api-bucket.s3-website.ap-northeast-2.amazonaws.com/)

## 🎲 주요 기능
### 경매 생성 로직
![image](https://github.com/user-attachments/assets/41487f3f-648e-484f-9790-666bc563e031)

<br>

### 경매 입찰 로직
![image](https://github.com/user-attachments/assets/eabf19bd-c702-4627-bd78-892bce9a83b6)

<br>

## 🧭[기술적 의사결정](https://www.notion.so/1e83dcf250078033b6facf83fbd65b47?pvs=4)
<details>
<summary>💎 <strong>Terraform 도입 이유</strong></summary>

### 도입 배경

- VPC, Subnet, ALB, ECS Fargate, RDS, OpenSearch 등 복잡한 AWS 리소스 구성 필요
- AWS 콘솔이나 CLI로 수동 설정 시 다음과 같은 문제 발생
  - 사람의 실수(Human Error)
  - 반복 작업으로 인한 시간 낭비
  - 환경 간 인프라 일관성 유지 어려움
  - 변경 이력 관리 및 협업의 어려움

### 선택지 검토

- **AWS CloudFormation**
  - 유지보수성과 가독성이 낮고, 조건/반복문 구현이 어렵고 AWS 종속적이라 미채택

- **AWS CDK**
  - 추상화 계층으로 인해 예측과 디버깅이 어렵고, 선언형 IaC 방식 선호로 인해 미채택

- **Pulumi**
  - 멀티 클라우드 지원은 강력하나 Terraform 대비 커뮤니티와 안정성에서 부족

### 최종 결정

- **Terraform 채택**
  - HCL 기반 선언적 구성: 가독성, 유지보수 용이
  - 성숙한 생태계: 풍부한 문서와 검증된 모듈 활용 가능
  - 멀티 클라우드 확장성 확보

</details>

<details>
<summary>💎 <strong>ECS Fargate 도입 이유</strong></summary>

### 도입 배경

- Docker 기반 Spring Boot 앱을 배포하면서 EC2 기반 인프라의 높은 관리 부담 발생
- 탄력적인 트래픽 대응 및 고가용성 확보를 위해 서버리스 기반의 ECS Fargate 선택

### 선택지 검토

- **EKS**
  - 복잡한 운영 및 학습 난이도 문제로 미채택

- **AWS Lambda**
  - Spring Boot 앱과 호환성 떨어지고 콜드 스타트, 상태 관리 이슈로 부적합

### 도입 효과

- ECS Service Auto Scaling으로 트래픽 변화 대응
- 가용 영역에 걸친 Task 분산 → 고가용성 확보
- ALB 연동으로 헬스체크 및 트래픽 분산 가능

</details>

<details>
<summary>💎 <strong>AWS S3 + CloudFront 도입 이유</strong></summary>

### 도입 배경

- 고화질 이미지 포함 시 서버 저장/제공에 한계 발생
- 빠른 로딩과 안정적인 이미지 제공 필요

### 선택지 검토

- **로컬 저장소**: 저장 공간, 백업, 서버 부하 이슈로 미채택
- **S3 단독 사용**: 반복 요청 환경에서 응답 속도 최적화 한계
- **S3 + CloudFront 조합** 채택

### 최종 결정

- S3로 이미지 저장, CloudFront로 엣지 캐시 전달
- 빠르고 안정적인 사용자 이미지 로딩 제공

</details>

<details>
<summary>💎 <strong>증분 BigQuery ETL 파이프라인 도입 이유</strong></summary>

### 도입 배경

- 전체 데이터 매번 적재(Full Load) 시 시간/리소스 낭비 및 중복 발생
- 데이터 신뢰성 유지와 로딩 속도 향상을 위해 증분 방식 고려

### 데이터 분석

| 데이터셋 | 100 | 1,000 | 5,000 | 10,000 | 50,000 | 100,000 |
|----------|------|--------|--------|---------|----------|-----------|
| 처리시간 | 3743 | 4041   | 4649   | 6466    | 7213     | 8184      |
![Image](https://github.com/user-attachments/assets/230bc4db-c0e0-4f25-afc4-be3a97b2cfbf)

### 최종 결정

- **증분 적재 방식 채택**
  - 처리 시간 단축
  - 리소스 효율 증가
  - 데이터 중복 제거 및 신뢰성 향상

</details>

<details>
<summary>💎 <strong>AWS EventBridge 도입 이유 (쿠폰 만료)</strong></summary>

### 도입 배경

- 쿠폰 유효기간 만료 시 간편하고 확장 가능한 만료 처리 필요

### 선택지 검토

- **메시지 큐**: 유연하지만 직접 구현 필요, 운영 복잡
- **DB 스케줄러**: 단순하지만 대규모 처리에 부적합
- **Redis TTL**: 실시간 감지는 가능하나 유실 위험/효율성 낮음
- **AWS EventBridge** 채택

### 최종 결정

- EventBridge는 시간 기반 이벤트를 AWS가 관리
- 재시도/확장성 보장 + Lambda, SQS 등 연동 유리
- 자원 효율성 + 지연 최소화 + 모니터링/로깅까지 제공

</details>

<details>
<summary>💎 <strong>RestDocs API 도입 이유</strong></summary>

### 도입 배경

- 수동 문서 작성의 번거로움 및 API 변경 시 일관성 문제 발생

### 🔍 선택지 검토

- **Swagger**
  - 사용 편리하나 명세와 실제 코드 불일치 가능
  - 수동 관리 필요

- **RestDocs API** 채택
  - 테스트 기반 자동 문서 생성
  - 정확성, 자동화, 커스터마이징 가능

</details>

<details>
<summary>💎 <strong>실시간 경매 Redis 도입 이유</strong></summary>

### 도입 배경

- 실시간 경매에서 경매 자동 종료, 결제 트리거 등 이벤트 기반 설계 필요

### 선택지 검토

- **AWS EventBridge Scheduler**
  - 예약에 강점 있으나 실시간성 부족, 비용 발생, WebSocket 연동 복잡

- **Redis TTL + Pub/Sub** 채택
  - TTL 만료 → 실시간 감지 (`Keyspace Notification`)
  - 만료 이벤트 → Pub/Sub → 브로드캐스트 가능
  - 추가 비용 없이 내부 인프라에서 처리 가능

### 최종 결정

> 경매 종료를 TTL 기반으로 자동 처리하면서, **만료 감지와 동시에 실시간 브로드캐스트**가 가능한 구조

| 기능 요구사항                         | Redis TTL + Pub/Sub                          |
|--------------------------------------|----------------------------------------------|
| 경매 종료 감지                       | TTL 설정 + Keyspace Notification          |
| 실시간성 유지                        | Pub/Sub 수신 즉시 처리                    |
| 종료 이벤트 전체 사용자 브로드캐스트 | WebSocket 서버에서 실시간 전달            |
| 수평 확장 고려                       | 상태 공유 + 브로드캐스트 확장성 보장     |
</details>

## 🚨[트러블 슈팅](https://www.notion.so/1e83dcf2500780b5bfb6f714fdc30c23?pvs=4)
<details>
<summary>🔎 <strong>트랜잭션 커밋 전 실시간 경매방이 생성되는 문제</strong></summary>

### 문제 상황

- 경매 생성 과정에서 유효성 검증 실패 등으로 인해 **DB에 저장되지 않았음**
- 그러나 DB 트랜잭션 커밋 이전에 **WebSocket 서버에 경매방 생성 요청이 전달되어 방이 생성됨**
- 이후 재시도 시 `auctionId`가 달라지면서 **DB와 실시간 서버 간 auctionId 불일치 문제** 발생

---

### 원인 분석

- `@Transactional` 메서드 내에서 DB 커밋 이전에 실시간 서버로 **외부 요청(WebSocket 경매방 생성 요청)**이 전송됨
- 결과적으로 DB 트랜잭션이 실패하거나 롤백되는 경우에도 WebSocket 서버에는 **유령 경매방이 생성**되어 버림
- 이는 시스템 상태 불일치로 이어져 실시간 경매 기능에 오류를 유발

---

### 해결 방법

- **Spring의 `@TransactionalEventListener` 기능 활용**
  - 트랜잭션이 **정상적으로 커밋된 후에만** 이벤트 리스너가 실행되도록 구조 변경
- DB에 경매 정보가 **정상 저장 완료된 이후에만 WebSocket 서버에 방 생성 요청**을 보냄

</details>


## 🔑 [Key Summary](https://www.notion.so/teamsparta/2-ReSell-C2C-1e22dc3ef51480c8ae7cef082afa5911?pvs=4#1e72dc3ef51480e188a5d2021849e048)
### 1. 이미지 응답 속도 최적화
**1-1. 문제 원인** <br>
- 동시에 많은 사용자가 제품 이미지를 조회할 경우 지연이 발생
- AWS S3을 사용할 경우 사용자와 이미지가 저장되어 있는 서버의 물리적 거리 멀수록 지연 시간 증가

**1-2. 기술 도입** <br>
- AWS CloudFront를 도입하여 사용자와 가까운 엣지 서버에서 데이터를 가져옴으로써 응답 속도 개선
  
**1-3. 성능 비교** <br>
<table>
  <tr>
    <td align="center">
      <dev><b> </b></dev>
    </td>
    <td align="center">
      <dev><b>AWS S3</b></dev>
    </td>
    <td align="center">
      <dev><b>AWS CloudFront</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Samples</b></dev>
    </td>
    <td align="center">
      <dev><b>3000</b></dev>
    </td>
    <td align="center">
      <dev><b>3000</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Avg(ms)</b></dev>
    </td>
    <td align="center">
      <dev><b>6262</b></dev>
    </td>
    <td align="center">
      <dev><b>2903</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Min(ms)</b></dev>
    </td>
    <td align="center">
      <dev><b>119</b></dev>
    </td>
    <td align="center">
      <dev><b>98</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Max(ms)</b></dev>
    </td>
    <td align="center">
      <dev><b>32614</b></dev>
    </td>
    <td align="center">
      <dev><b>31935</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Error(%)</b></dev>
    </td>
    <td align="center">
      <dev><b>0</b></dev>
    </td>
    <td align="center">
      <dev><b>0</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Throughput(req/s)</b></dev>
    </td>
    <td align="center">
      <dev><b>72.2</b></dev>
    </td>
    <td align="center">
      <dev><b>76.5</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Received KB/s</b></dev>
    </td>
    <td align="center">
      <dev><b>36185.1</b></dev>
    </td>
    <td align="center">
      <dev><b>38330.6</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Sent KB/s</b></dev>
    </td>
    <td align="center">
      <dev><b>14.8</b></dev>
    </td>
    <td align="center">
      <dev><b>14.0</b></dev>
    </td>
  </tr>
</table>

<br>

<img src="https://github.com/user-attachments/assets/cda7905c-cb9b-4f76-ad55-fee47826dd0a" width="400" height="250"><br>

**1-4. 성능개선요약** <br>
- **평균 응답 시간**: 6262ms → 2903ms (약 54% 개선)
- **처리량**: 72.2 req/s -> 76.5 req/s (약 6% 개선)

<br>

### 2. 캐시별 조회 기능 개선
캐시가 미적용된 조회 기능과 Redis, Caffeine이 적용된 조회 기능을 테스트
- **테스트 주제**: 조회를 100번 시도 했을 때의 평균 응답 속도
- **테스트 결과**<br>
  <img src="https://github.com/user-attachments/assets/ee3f0b4d-4ad1-41b2-b61b-bc9ae1951f39" width="400" height="250"><br>
<table>
  <tr>
    <td align="center">
      <dev><b> </b></dev>
    </td>
    <td align="center">
      <dev><b>No Cache</b></dev>
    </td>
    <td align="center">
      <dev><b>Redis</b></dev>
    </td>
    <td align="center">
      <dev><b>Caffeine</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Avg(ms)</b></dev>
    </td>
    <td align="center">
      <dev><b>5.26</b></dev>
    </td>
    <td align="center">
      <dev><b>10.05</b></dev>
    </td>
    <td align="center">
      <dev><b>0.12</b></dev>
    </td>
  </tr>
</table>

Caffeine → No Cache → Redis 순으로 응답 속도가 빠름

- **의문점**
  1. 왜 Redis가 느린가?<br>
     Redis는 기본적으로 외부 서버와 TCP 통신을 하기 때문<br>
     1. Java 객체 → JSON 직렬화
     2. Redis로 네트워크 전송
     3. 다시 역질렬화해서 Java 객체로 복구
  반면 Caffeine은 직접 JVM 메모리에서 객체를 바로 꺼내기 때문에 Redis보다 훨씬 빠름

  2. 그럼 No Cache보다 느린가?
     - 데이터가 작고 DB가 로컬에 있으면 단순 쿼리 실행이 Redis 통신보다 빠를 수도 있음
     - Redis는 캐시할 때 Jackson 직렬화/역직렬화가 항상 개임하기 때문에 그만큼 시간이 더 걸리게 됨
     - 캐시 미스가 발생해서 Redis가 무의미하게 조회되고 있을 수도 있음

  즉, 속도만 비교하면 Redis보다 Caffeine이 좀 더 우위에 있음을 알 수 있음

<br>

### 3. 캐시별 조회 기능 개선
MySQL vs 로컬 Elasticsearch vs AWS OpenSearch(퍼블릭 도메인)
- **테스트 방식**: 10000건의 더미 경매 생성 후 경매 목록 검색 속도 비교
- **테스트 결과**<br>
  <img src="https://github.com/user-attachments/assets/2e353298-8c71-4b04-b677-313303d1cb1b" width="400" height="250"><br>
<table>
  <tr>
    <td align="center">
      <dev><b> </b></dev>
    </td>
    <td align="center">
      <dev><b>MySQL</b></dev>
    </td>
    <td align="center">
      <dev><b>Elasticsearch</b></dev>
    </td>
    <td align="center">
      <dev><b>OpenSearch</b></dev>
    </td>
  </tr>
  <tr>
    <td align="center">
      <dev><b>Avg(ms)</b></dev>
    </td>
    <td align="center">
      <dev><b>388</b></dev>
    </td>
    <td align="center">
      <dev><b>245</b></dev>
    </td>
    <td align="center">
      <dev><b>528</b></dev>
    </td>
  </tr>
</table>

- **의문점**
  - 어째서 OpenSearch의 소요 시간이 남들보다 훨씬 더 걸리는가?

  - 원인 분석
    원인은 AWS 외부와 통신이필요한 구조이기 때문임<br>
    로컬 Elasticsearch는 로컬이기 때문에 네트워크 지연이 없지만<br>
    AWS OpenSearch의 경우 AWS 외부와 통신을 하기 때문에 외부 호출로 인해서 시간 소요가 증가함

<br>

### 4. Docker 이미지 최적화를 통한 빌드 및 배포 효율성 증대

- **배경**
  - CI/CD 파이프라인의 효율성을 높이고 배포 시간을 단축하며, 저장 공간 및 네트워크 비용을 절감하는 것의 필요성을 확인.

- **적용된 최적화 기술**
  - 멀티 스테이지 빌드.
  - Docker 레이어 캐싱 활용.
  - 최소한의 런타임 이미지 사용 (jre).
  - 빌드 시 테스트 제외.
  → 빌드 단계는 이미지 생성에만 집중하고, 테스트는 다른 단계에서 처리하여 전체 과정을 효율적으로 만들기 위함.

-** 개선 결과 **
  - 이미지 빌드 시간: 1분 12초 → 1분 1초 (약 15.3% 단축)
  - 이미지 크기: 450.72 MB → 122.33 MB (약 73% 감소)

-** 최적화 도입 전 후의 결과 비교 **

| 항목             | 최적화 전 | 최적화 후 | 개선 효과       |
|----------------|---------|---------|---------------|
| 이미지 빌드 시간 | 1분 12초 | 1분 1초  | 11초 단축     |
| 이미지 크기       | 450.72 MB | 122.33 MB | 328.39 MB 감소 |

![image](https://github.com/user-attachments/assets/86bffb89-0143-4f63-9e65-fb889af4e8f1)


- **효과**
  - **CI/CD 파이프라인 효율성 증대:**
    - 이미지 빌드 시간이 **약 15.3% 단축**되어 (1분 12초 → 1분 1초) 개발 및 배포 사이클이 단축.
    - 빌드 단계에서 테스트를 분리하여 파이프라인의 각 단계를 명확히 하고 효율성을 높임.
  - **배포 속도 향상 및 비용 절감:**
    - 이미지 크기가 **약 73% 감소**하여 (450.72 MB → 122.33 MB), 컨테이너 레지스트리로의 이미지 푸시 및 배포 서버에서의 이미지 풀 속도 향상.
    - 이미지 크기 감소는 레지스트리 **저장 공간 비용** 및 이미지 전송에 따른 **네트워크 비용** 절감.
  - **리소스 사용 최적화:**
    - 최소한의 런타임 이미지 사용과 최적화된 빌드 과정은 최종 이미지의 크기를 줄여 서버 **디스크 공간 절약.**
      
- **개선 결과**
  - 이미지 빌드 시간: 1분 12초 → 1분 1초 (약 15.3% 단축)
  - 이미지 크기: 450.72 MB → 122.33 MB (약 73% 감소)


### 5. BigQuery+GA4를 활용한 인사이트 도출 & 데이터 시각화
- **목표**
  - Google Analytics 4 (GA4)에서 수집된 사용자 데이터와 애플리케이션의 경매 낙찰가 데이터를 Google BigQuery 환경에서 통합 분석.
  - 카테고리별 특성 파악, 시계열 트렌드 분석 등 비즈니스 의사결정에 기여할 수 있는 심층적인 인사이트 도출.

- **데이터 분석 접근 방식**
  - **데이터 통합**: GA4의 Raw 데이터를 BigQuery로 Export하고, 내부 경매 시스템의 데이터와 결합하여 사용자 세션부터 최종 구매(낙찰)까지 이어지는 통합 데이터셋 구축.
  - **데이터 분석**: BigQuery의 SQL 쿼리를 활용하여 대규모 데이터를 대상으로 사용자 행동 패턴, 구매 전환율, 카테고리별 선호도, 시간별 트렌드 등 다양한 지표 분석.
  - **인사이트 시각화**: 분석 결과를 직관적으로 이해하고 공유할 수 있도록 주요 지표들을 시각화하여 대시보드 형태로 구성.
    
- **데이터 시각화**

![image](https://github.com/user-attachments/assets/e84f3540-d32c-4c6b-b06d-802e0b01dacb)

