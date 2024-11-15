# 도서 대여 관리 시스템

#### 프로젝트 설명
> 이 프로젝트는 JPA 성능 최적화 학습을 위한 프로젝트입니다.

도서 대여 시스템으로써 사용자(User), 도서(Book), 대여(Loan) 엔티티를 가지며 기본적인 CRUD 기능을 제공합니다.

#### 기술 스택
- Open JDK 17
- Spring Boot 3.3.5
- H2 Database ( 테스트 용 )
- Gradle
- Spring Data JPA & QueryDsl

#### 주요 엔티티 구조
1. Member: 사용자 정보 ( 이름, 이메일, 전화번호 )
2. Book: 도서 정보 ( 제목, 저자, 출판사 )
3. Loan: 대여 정보 ( 대여일, 반납일, 연체 여부 )

#### 주요 엔티티 관계
- Member와 Loan 관계 : 1 : N ( 한명의 사용자는 여러개를 대여할 수 있다. )
- Book과 Loan 관계 : 1 : N ( 한권의 책은 여러번 대여될 수 있다. )

#### 주요 기능
1. 도서 검색 : 제목, 저자, 출판사 등으로 책을 검색할 수 있다.
2. 사용자 검색 : 이름, 이메일, 전화번호 등으로 사용자를 검색할 수 있다. 
3. 대여 및 반납 : 사용자가 특정 책을 대여하거나 반납할 수 있다. 
4. 대여 내역 조회 : 각 사용자는 자신의 대여 내역(연체 포함)을 조회 할 수 있다.
