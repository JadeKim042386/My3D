# My3D

## Project Structure

![](./imgs/my3d_project_structure.svg)

## Development Environment

- Intellij IDEA Ultimate 2023.2.4
- Java 17
- Gradle 8.2.1
- Spring Boot 2.7.14

## Tech Stack

| FrontEnd                                                                                                              |
|-----------------------------------------------------------------------------------------------------------------------|
| ![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white) | 
|️ ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white) |

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

## Flow Chart

1. [회원가입](#1-회원가입)
2. [로그인](#2-로그인)
3. [Authentication (인증)](#3-authentication-인증)
4. [게시글 작성](#4-게시글-작성)
5. [게시글 수정](#5-게시글-수정)
6. [좋아요 기능](#6-좋아요-기능-user-a가-b-게시물에-좋아요를-누른-상황) 
7. [댓글 기능](#7-댓글-기능-user-a가-b-게시물에-댓글을-남긴-상황)

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

### 4. 게시글 작성

```mermaid
sequenceDiagram
    autonumber
    actor client
    client ->>+ WAS: 게시글 작성 요청
    activate client
    WAS ->> WAS: validation
    WAS ->> DB: 게시글 저장 요청
    activate DB
    WAS ->> DB: 파일 저장 요청
    deactivate DB
    WAS -->>- client: 
    deactivate client
```

### 5. 게시글 수정

```mermaid
sequenceDiagram
    autonumber
    client ->> WAS: 게시글 수정 요청
    activate client
    activate WAS
    WAS ->> WAS: validation
    alt 파일이 수정되었을 경우
        WAS ->> DB: 이전 파일 삭제 요청
        activate DB
        WAS ->> DB: 수정된 파일 저장 요청
    end
    WAS ->> DB: 게시글 수정 요청
    deactivate DB
    WAS -->> client: 
    deactivate WAS
    deactivate client
```

### 6. 좋아요 기능: User A가 B 게시물에 좋아요를 누른 상황

```mermaid
  sequenceDiagram
    autonumber
    actor client A
    alt 좋아요를 추가한 경우
        client A ->>+ WAS: 좋아요 요청
        WAS ->> WAS: 게시글 좋아요 수 + 1
        WAS ->> DB: 게시글의 좋아요 개수 수정
    else 좋아요를 취소한 경우
        client A ->>+ WAS: 좋아요 취소 요청
        WAS ->> WAS: 게시글 좋아요 수 - 1
        WAS ->> DB: 게시글의 좋아요 개수 수정
    end
```

### 7. 댓글 기능: User A가 B 게시물에 댓글을 남긴 상황

```mermaid
sequenceDiagram
    autonumber
    actor client A
    client A ->>+ WAS: 댓글 작성
    WAS ->> WAS: 대댓글인지 확인
    alt 대댓글인 경우
        WAS ->> DB: 부모 댓글에 대댓글 추가
        activate DB
        WAS ->> DB: 대댓글 저장 요청
    else 대댓글이 아닌 경우
        WAS ->> DB: 댓글 저장 요청
    end
    WAS ->> DB: 댓글 알람 저장 요청
    deactivate DB
    actor client B
    WAS -->>- client B: 알람 전송
```
