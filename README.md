# My3D

## Flow Chart

1. [회원가입](#1-회원가입)
2. [로그인](#2-로그인)
3. [Authentication (인증)](#3-authentication-인증)
4. [게시글 작성](#4-게시글-작성)
5. [게시글 삭제](#5-게시글-삭제)
6. [게시글 수정](#6-게시글-수정)
7. [좋아요 기능](#7-좋아요-기능-user-a가-b-게시물에-좋아요를-누른-상황) 
8. [댓글 기능](#8-댓글-기능-user-a가-b-게시물에-댓글을-남긴-상황)
9. [주문 기능](#9-주문-기능-user-a가-b에게-주문을-요청한-상황)

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
    WAS ->> DB: 상품 옵션&치수 저장 요청
    deactivate DB
    WAS -->>- client: 
    deactivate client
```

### 5. 게시글 삭제

```mermaid
sequenceDiagram
    autonumber
    actor client
    client ->>+ WAS: 게시글 삭제 요청
    activate client
    WAS ->> DB: 파일 데이터 삭제 요청
    activate DB
    WAS ->> DB: 댓글 데이터 삭제 요청
    WAS ->> DB: 좋아요 데이터 삭제 요청
    WAS ->> DB: 게시글 데이터 삭제 요청
    deactivate DB
    WAS -->>- client: 
    deactivate client
```

### 6. 게시글 수정

```mermaid
sequenceDiagram
    autonumber
    client ->> WAS: 게시글 수정 요청
    activate client
    activate WAS
    WAS ->> WAS: validation
    alt 파일이 수정되었을 경우
        WAS ->> DB: 게시글에 포함된 모든 파일 삭제 요청
        activate DB
        WAS ->> DB: 수정된 파일 저장 요청
    end
    WAS ->> DB: 게시글에 포함된 모든 상품 옵션&치수 삭제 요청
    WAS ->> DB: 수정된 상품 옵션&치수 저장 요청
    WAS ->> DB: 게시글 수정 요청
    deactivate DB
    WAS -->> client: 
    deactivate WAS
    deactivate client
```

### 7. 좋아요 기능: User A가 B 게시물에 좋아요를 누른 상황

```mermaid
  sequenceDiagram
    autonumber
    actor client A
    client A ->>+ WAS: 좋아요 요청 
    WAS ->> WAS: 게시글 좋아요 수 + 1
    WAS ->> DB: 게시글 수정 요청
    activate DB
    WAS ->> DB: 좋아요 알람 저장 요청
    deactivate DB
    actor client B
    WAS -->>- client B: 알람 전송
```

### 8. 댓글 기능: User A가 B 게시물에 댓글을 남긴 상황

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

### 9. 주문 기능: User A가 B에게 주문을 요청한 상황

```mermaid
sequenceDiagram
    autonumber
    actor client A
    client A ->>+ WAS: 주문 요청
    WAS ->> DB: 주문 저장 요청
    activate DB
    WAS ->> DB: 주문 알람 저장 요청
    deactivate DB
    actor client B
    WAS -->>- client B: 알람 전송
```
