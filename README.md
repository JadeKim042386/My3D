# My3D

## Flow Chart

1. [회원가입](#1-회원가입)
2. [로그인](#2-로그인)
3. [Authentication (인증)](#3-authentication-인증)
4. 게시글 작성
5. 게시글 삭제
6. 게시글 수정
7. 게시글 파일 저장
8. 좋아요 기능 
9. 댓글 기능
10. 알람 기능
11. 주문 기능

### 1. 회원가입

```mermaid
  sequenceDiagram
    autonumber
    client ->> 회원 유형 선택: 회원가입
    alt 기업/기관일 경우
        회원 유형 선택 -->> 사업자 인증: 기업/기관 선택
        사업자 인증 -->> 회원 정보 입력: 사업자 인증
        alt 성공한 경우
            사업자 인증 -->> 회원 정보 입력: 회원 정보 입력 페이지로 이동
        else 실패한 경우
            회원 정보 입력 -->> 사업자 인증: 사업자 인증 페이지로 redirect 후 에러 메시지 표시
        end
    else 개인일 경우
        회원 유형 선택 -->> 회원 정보 입력: 개인 선택
    end
    회원 정보 입력 ->> 이메일 인증: 이메일 인증 요청
    alt 인증 성공한 경우
        이메일 인증 -->> 회원 정보 입력: 인증 성공 메시지 표시
    else 인증 실패한 경우
        이메일 인증 -->> 회원 정보 입력: 인증 실패 메시지 표시
    end
    회원 정보 입력 ->> server: 회원가입 요청
    alt 성공한 경우
        회원 정보 입력 -->> server: 홈페이지로 이동
    else 실패한 경우
        server -->> 회원 정보 입력: 회원가입 페이지로 redirect 후 에러 메시지 표시
    end
```

### 2. 로그인

```mermaid
sequenceDiagram
    autonumber
    client ->> WAS: 로그인 요청
    WAS ->> DB: 유저 정보 요청
    alt 성공한 경우
        DB -->> WAS: 유저 정보 반환
        WAS ->> client: 비밀번호 일치 여부 확인
        alt 일치하는 경우
            WAS -->> client: JWT 토큰 생성 후 쿠키에 저장하고 로그인 이전 페이지로 이동
        else 일치하지 않는 경우
            WAS -->> client: Exception 처리 및 로그인 페이지로 Redirect
        end
    else 실패한 경우
        DB -->> WAS: null 반환
        WAS -->> client: Exception 처리 및 로그인 페이지로 Redirect
    end
    
```

### 3. Authentication (인증)

```mermaid
sequenceDiagram
    autonumber
    client ->> WAS: authentication 요청
    WAS ->> client: JWT 토큰이 있는 쿠키 존재 여부 확인
    alt 쿠키가 존재하는 경우
        WAS ->> client: 쿠키에서 JWT 토큰을 확인하여 만료 확인
        alt 만료되지 않았을 경우
            WAS ->> DB: 유저 정보 요청
            alt 성공한 경우
                DB -->> WAS: 유저 정보 반환
                WAS ->> client: 인증 성공
            else 실패한 경우
                DB -->> WAS: null 반환
            end
        else 만료되었을 경우
            WAS -->> client: 인증 실패 및 로그인 페이지로 이동
        end
    else 쿠키가 존재하지않는 경우
        WAS -->> client: 인증 실패 및 로그인 페이지로 이동
    end
```
