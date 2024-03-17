# My3D

3D 모델 중에는 재사용 가능한 것들이 많습니다. 그래서 누구나 3D 모델을 공유하여 사용할 수 있도록하는 서비스입니다.

- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Preview](#preview)
- [Flow Chart](#flow-chart)
- [ERD](#erd)

## Project Structure

![](./imgs/my3d_project_structure.svg)

## Development Environment

- Intellij IDEA Ultimate 2023.2.4
- Java 17
- Gradle 8.2.1
- Spring Boot 2.7.14

## Tech Stack

| FrontEnd                                                                                                                                                                                                                                                                                                                      |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![js](https://img.shields.io/badge/javascript-%23F7DF1E.svg?style=for-the-badge&logo=javascript&logoColor=black) ![html](https://img.shields.io/badge/HTML-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![css](https://img.shields.io/badge/CSS-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) |
| ![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white)                                                                                                                                                                                                         | 
| ️ ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)                                                                                                                                                                                                       |

| BackEnd                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![SpringBoot](https://img.shields.io/badge/SPRINGBOOT-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![SpringSecurity](https://img.shields.io/badge/SPRINGSECURITY-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) |
| ![SpringDataJpa](https://img.shields.io/badge/SPRING_DATA_JPA-6DB33F?style=for-the-badge) ![QueryDSL](https://img.shields.io/badge/QueryDSL-009DB8?style=for-the-badge) |
| ![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white) ![Mockito](https://img.shields.io/badge/Mockito-25A162?style=for-the-badge)|
| ![intellijidea](https://img.shields.io/badge/intellij_idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| ![git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |

| DevOps                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white) ![S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white) ![RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white) |
| ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)                                                                                                                                                                                                                                 |
| ![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)                                                                                                                                                                                                                                                                                                                                                                                                                                          |

| 자동 배포화                                                                                                                                                                                                                                                             | 
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white) ![CODEDEPLOY](https://img.shields.io/badge/CODEDEPLOY-%232671E5?style=for-the-badge) |

## Preview

### 메인 페이지

- 카테고리별 게시글 검색 가능
- 좋아요, 작성일자 기준 게시글 정렬 가능
- 게시글 제목 검색 가능

![](./imgs/main_page.png)

### 게시글 페이지

- 게시글 수정/삭제 가능
- 좋아요 추가/삭제 가능
- 파일 다운로드 가능
- 댓글/대댓글 작성 가능
- 댓글 작성시 알람 전송

![](./imgs/article_page.png)

### 게시글 작성 페이지

- 제목, 본문, 파일, 치수 정보, 카테고리 입력

![](./imgs/article_write_page.gif)

### 로그인 & 비밀번호 찾기 페이지

- 구글, 네이버, 카카오 로그인 가능
- 비밀번호 찾기 가능

![](./imgs/login_page.gif)

### 회원가입 페이지

- 기업 유저는 사업자 인증을 수행
- 기업/일반 유저는 본인 인증을 목적으로 이메일 인증을 수행

![](./imgs/signup_page.gif)

### 유저 정보 수정 페이지

- 일반 유저와 기업 유저 구분
- 기업 유저는 기업 정보를 수정할 수 있는 페이지가 추가됨

![](./imgs/update_user_page.gif)

## Flow Chart

1. [회원가입](#1-회원가입)
2. [로그인](#2-로그인)
3. [Authentication (인증)](#3-authentication-인증)
4. [알람](#4-알람)

### 1. 회원가입

```mermaid
  sequenceDiagram
    autonumber
    actor client
    client ->>+ 회원 유형 선택: 회원가입
    activate client
    alt 기업/기관일 경우
        회원 유형 선택 ->>+ 사업자 인증: 사업자 인증 페이지 요청
        사업자 인증 ->> 사업자 인증: 사업자 인증
        사업자 인증 ->>- 회원 정보 입력: 회원가입 페이지 요청
        activate 회원 정보 입력
    else 개인일 경우
        회원 유형 선택 ->> 회원 정보 입력: 회원가입 페이지 요청
    end
    회원 정보 입력 ->> 회원 정보 입력: 이메일 인증
    회원 정보 입력 ->>+ WAS: 회원가입 요청
    deactivate 회원 정보 입력
    WAS ->> WAS: validation
    WAS ->> DB: 유저 정보 저장
    WAS ->> WAS: JWT 토큰 생성
    WAS -->>- client: JWT 토큰 전달
    client ->> client: 홈페이지 redirect 
    deactivate client
```

### 2. 로그인

```mermaid
sequenceDiagram
    autonumber
    actor client
    client ->> WAS: 로그인 요청
    activate client
    WAS ->>+ DB: 유저 정보 요청
    activate WAS
    DB -->>- WAS: 유저 정보 반환
    WAS ->> WAS: 비밀번호 일치 확인
    WAS ->> WAS: JWT 토큰, Refresh Token 생성
    WAS -->>- client: Token 전달
    client ->> client: 홈페이지로 이동
    deactivate client
```

### 3. Authentication (인증)

```mermaid
sequenceDiagram
    autonumber
    actor client
    client ->> WAS: Authentication 요청
    activate client
    activate WAS
    WAS ->> WAS: JWT 토큰 확인
    alt JWT 토큰이 유효한 경우
        WAS ->> WAS: 토큰 정보로 UserDetail 객체 생성
        WAS ->> WAS: Authentication
        WAS -->> client: 인증 완료
    else JWT 토큰이 만료된 경우
        WAS ->> WAS: Refresh Token parsing & validation
        alt Refresh Token이 유효한 경우
            WAS ->> WAS: JWT 재발행
            WAS ->> WAS: Authentication
            WAS -->> client: 재발행된 JWT 토큰 전달
        else Refresh Token이 유효하지않은 경우
            WAS ->> client: 로그인 페이지 이동
        end
    else JWT 토큰이 유효하지않은 경우
        WAS -->> client: 로그인 페이지 이동
        deactivate WAS
        deactivate client
    end
```

### 4. 알람

#### 4.1. 페이지 전환
```mermaid
sequenceDiagram
    autonumber
    actor client
    client ->> WAS: GET /api/v1/alarm
    activate client
    activate WAS
    WAS ->>+ DB: 알람 정보 요청
    DB -->>- WAS: 알람 정보 반환
    WAS -->>- client: 알람 정보 전달
    deactivate client
```
#### 4.2. 이벤트 발생 (댓글 작성)
```mermaid
sequenceDiagram
    autonumber
    actor client
    activate client
    WAS ->>+ DB: 댓글 저장
    activate WAS
    WAS ->> DB: 알람 저장
    deactivate DB
    alt 알람 수신자가 로그인 상태인 경우
      WAS -->>- client: 알람 정보 전달 (SseEmitter, Websocket)
    end
    deactivate client
```

## ERD

- 이미지를 클릭하면 ERDCloud 페이지로 이동합니다.

[![ERD](./imgs/my3d-erd.png)](https://www.erdcloud.com/p/z2Xw9K4cA8buyzSZQ)
