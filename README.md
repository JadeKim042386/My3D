# My3D

## Project Structure

![](./imgs/my3d_project_structure.svg)

## Development Environment

- Intellij IDEA Ultimate 2023.2.4
- Java 17
- Gradle 8.2.1
- Spring Boot 2.7.14

## Tech Stack

#### FrontEnd  

- Framework  
![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white)

#### BackEnd

- Framework  
![SpringBoot](https://camo.githubusercontent.com/cd0c88ca6f43cc79094ccce27ef779ce3b5a5a4086a30420b68226185bdbe1e2/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f737072696e67626f6f742d3644423333463f7374796c653d666f722d7468652d6261646765266c6f676f3d737072696e67626f6f74266c6f676f436f6c6f723d7768697465)
- Template Engine  
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
- Security  
![SpringSecurity](https://img.shields.io/badge/SPRINGSECURITY-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
- DB  
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)

#### PaaS 

![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
- Server  
![EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
- Storage  
![S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
- DB  
![RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)

#### Web Server

- Reverse Proxy  
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)


#### CI/CD
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)

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
    WAS -->>- client: 
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
    WAS ->> WAS: JWT 토큰 생성
    WAS -->>- client: 
    client ->> client: 로그인 이전 페이지로 이동
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
        WAS ->>+ DB: 유저 정보 요청
        DB -->>- WAS: 유저 정보 반환
        WAS ->> WAS: Authentication 저장
        WAS -->> client: 인증 완료
    else JWT 토큰이 유효하지않은 경우
        WAS -->> client: 
        deactivate WAS
        client ->> client: 로그인 페이지 이동
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
