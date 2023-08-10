# 지연로딩과 프록시
## 프록시
    1.em.find(Member.class,m1.getId()), em.getReference(Member.class, m.getId())
        두 가지 방법으로 객체를 만든다.
        전자는 진성으로 db를 조회하여 만든 엔티티 객체이고
        후자는 정말 엔티티를 참조할일(getUserName()등)이 있는 경우에만 영속성컨텍스트를 통해 
        엔티티 객체를 참조하는 녀석이다.
    2. em.getReference() -> 프록시 객체 반환
        프록시 객체는 바로 엔티티객체를 조회하지 않는다.
        대신 정말 참조가 발생하는 경우에만 영속성 컨텍스트를 통하여 디비를 조회한다.
        따라서 em.close(),em.detatch(proxyEntity), em.clear()로 각각 
        영속성 컨텍스트를 종료, 준영속상태로 전환, 영속성 컨텍스트 정리를 할 경우 에러가 발생한다.(Lazy 뭐시기)
    3. 또한 @Transaction 혹은 영속성 컨텍스트 내에서 
        엔티티객체-프록시 객체, 엔티티-엔티티, 프록시-프록시등 서로 같거나 상이한 타입을 비교하더라도
        JPA는 같은 타입이 되도록 해준다. 허나, 서로 다른 영속성 컨텍스트에 위치 한다면 == 연산이 false가 된다.
        따라서 항상 '(프록시,엔티티) isinstanceof (타입)'으로 비교하도록 하자
    4. 프록시 객체는 지연, 즉시 로딩을 이해하는 기본이다.
## 지연로딩, 즉시로딩
    지연로딩(Lazy loading)은 xToOne관계에서 연관된 엔티티를 직접 가져오지 않는다.
    앞서 배운 프록시 객체로 가져와두고, 실제 참조 할때 엔티티 객체를 참조한다.
    순서: 영속성 컨텍스트 -> db 조회 -> entity객체 생성 -> 생성된 entity객체 참조
    즉 한번 프록시 객체인 상태에서는 계속 프록시 객체이다.
    
    즉시로딩(Eager loading): 'select m from Member m'등의 단일 테이블 조회 쿼리 발생시에도
    반환후 그 즉시 연관관계 테이블의 엔티티들을 몽땅 조회한다.
    만일 한 테이블의 연관관계 테이블이 10개라고 생각해보자.
    쿼리문의 길이는 안드로메다롤 향한다. + 1+N 문제의 주범 -> 그런데 요즘은 조인으로 다가져 오는거 같다.
    !!! 조인문으로 가져오더라도 여러개의 테이블을 동시에 조회시 성능 약화를 기대할 수있다.
    
    전략: 모든 연관관계는 lazy로딩으로 한다.
    조인의 해결:
        join fetch, entity 그래프 이용하여 join쿼리로 해결한다.
## 영속 컨텍스트 전이 밑 orphanRemoval=true WITH DDD.AggregateRoot
    1. xToOne의 cascade=CascadeType.All등으로 연관 매핑된 녀석과 함께 움직인다.
        em.persist, em.remove등이 적용된 엔티티와 그 매핑된 녀석이 함께 적용
    2. orphanRemoval=true에 의하여 콜렉션 등에서 빠지면 바로 제거 된다.
    *3. 적용할 조건: 하나의 엔티티만 소유하고 있을때, 라이프 사이클이 같을때.
    4. DDD의 aggregate root개념을 충실히 적용할 수 있다. -> 부모 엔티티의 리포지토리로 자식 엔티티의 레포지토리 구현없이 관리한다는 개념이다.
## 값 타입-임베디드
    1. 공통속성을 덩어리로 관리하는 객체이다. 엔티티 내부의 속성으로 사용한다.
    2. @Embeded(usage in entity), @Embedable(define)로 정의, 엔티티 내에서 사용한다.
        @NoArgsConstructor 필수
    3. 중복하여 속성으로 사용될때는 @AttributeOverrides{@AttributeOverride로 사용한다.}
    4. 불변 객체로 정의해야한다(setter제거)
        Address address = new Address("city","street","zipcode");
        Member m1 = new Member()
        Member m2 = new Member()
        m1.setAddress(address)
        m2.setAddress(address)
        m1.getAddress().setCity("dragon")
        -> m2에도 영향이간다.
    5. 값 타입 비교: 동일성(identity)비교가 기본이므로 동등성 비교를 하도록 Object.equals 
        메소드를 동등성(equivalence)비교를 하도록 재정의 해주어야한다.

# JPQL(Java Persistence Query Language)
## jpql?
    의의: 기존에 sql은 테이블을 대상으로 작성해야한다.
        jpql은 객체를 대상으로 db query를 작성할 수 있다.
    한계점:
        intellij가 도와주어서 불편함이 많이 개선되나, 
        쿼리문은 기본적으로 문자열(String)이다. 따라서 컴파일 시점에 잡아 줄 수 없다.
        이 문제는 동적 쿼리를 작성할때 너무나 힘들어진다.
    대안:
        jpa는 criteria를 지원함으로써 해결하려하였다.
        그러나 떨어지는 가독성 불편한 사용법등으로 bad case && bad practice이다.
        
        컴파일 시점 오류 체크, 쉬운 인터페이스를 자랑하는 queryDSL을 적극 사용하자.
    부연: 
        createNativeQuery로 네이티브 sql을 작성할 수도 있다 ㅎㅎ
## jpql-basic[(TypedQuery<T>, Query),set parameter, getResultList,getSingleResult]
    1. (TypedQuery<T>, Query)
        타입드 쿼리의 경우 명확한 타입임이 보장 될떄
        TypedQuery<String> = em.createQuery("select m.username from Member m, String);
        
        쿼리의 경우 반환 타입이 명확하지 않을때, createQuery의 인자로 하나(jpql)만 넘긴다.
        Query query=  em.createQuery("select m.username m.age from Member m);
    2. setParameter(): where 검색조건이 주어질때
        2-1: name 기반
        em.createQuery("select m from Member m where m.username = :username", String)
            .setParameter("username", "dragon")
            .getResultList();
        2-2: 위치 기반 : 비추한다 => 만일 중간에 조건이 끼어들면 쭉 밀려버리기 때문이다.
        em.createQuery("select m from Member m where m.username = ?1", String)
            .setParameter(1, "dragon")
            .getResultList();
    3. getResultList,getSingleResult
        3-1 getResultList: 조회건이 없으면 빈 리스트 반환
        3-2 getSingleResult: 조회건이 없으면 없다고 에러, 하나 이상이면 하나 이상이라고 에러
## jpql-basic[projection]
    0. *** em.flush()가 실행되는 시점: em.commit(), query날라가는 경우
    엔티티,매핑 엔티티,임베디드,스칼라 타입(필드 여러개-Query,필드 하나-typedQuery) 중복제거 distinct
    1. select m from Member m
    2. select m.team from Member m -> 묵시적 조인: 정확한 예측이 어려우므로 지양할것
    2-1. select t from Member m join fetch m.team t : 명시적 조인 (추천 방법)
    3. select m.address from Member m   
    4. em.createQuery("select m.name, m.age from Member m") -> Query 타입이다.
        4-1 Object로 반환: 
        4-2 Object[] 로 반환:
            List<Object[]> res = em.createQuery("select m.id ,m.username from Member m")
                .getResultList();
        4-3 dto로 매핑 반환:
            List<MemberDto> resultList = em.createQuery("select new jpashop.dto.MemberDto(m.id,m.username) from Member m",
                MemberDto.class)
            .getResultList();
## jpql-basic[paging]
    드러운 row sql 작성 필요없이 setFirstResult(), .setMaxResults(10)로 가능
    List<People> resultList = em.createQuery(
        "select m from People m order by m.id",
        People.class
    )
        .setFirstResult(1)
        .setMaxResults(10)
        .getResultList();
## jpql-join[inner, outer, cross join(막 조인== 연관관계 없는),on(조회 조건 필터링)]
    1. [inner] join:
    jpql: select m,t from Member m join m.team t ON t.name='A'
    sql: select m.*, t.* from Member m join Team t On m.team_id = t._id and t.name='A'
    2. left [outer] join
    jpql: select m from Member m left join m.team
    3. cross join (pk, foreign key 매핑절이 추가 되지 않고 jpql과 같은 꼴로 그대로 나간다)
    jpql: select m,t from Member m left join Team t on m.username = t.team_name
    sql: select m.*, t.* from Member m left join Team t on m.username = t.team_name
## jpql-subquery
    1. 나이가 평균보다 많은 회원
    jpql: select m from Member m where m.age > (select avg(m1.name) from Member m1);
    2. 한 건이라도 주문한 고객
    jpql: select m from Member m where (select o from Order o where m = o.member)
## jpql- 서브 쿼리 지원함수 exist, all, (any,some)
    1. [NOT] EXIST (subquery) : subquery에 포함되면 참
    팀 A소속인 회원
    jpql: select m from Member m where exist(select t from m.team where t.name='A')
## jpql-type expression
    types: String => 'Hello', Boolean => true,false, Number => 10L,10D(double),10F(float)
    List<Object[]> result = em.createQuery(
        "select m.name, 'Hello', true from Member m"
    ).getResultList();
    for(Object o  : result){
        sout(o[0] +o[1] + o[2])
    }
    
    enum type: package+class까지 다 표기 해주어야한다.
        List<Object[]> result = em.createQuery(
        "select m.name, 'Hello', true from Member m "+
        "where m.type = jpql.MemberType.USER"
    ).getResultList();
        
    setParameter로 단순화 하면 아래와 같다.
        List<Object[]> result = em.createQuery(
        "select m.name, 'Hello', true from Member m "+
        "where m.type = :type"
    ).setParameter("type",MemberType.USER)
    .getResultList();
## jpql-type expression type():상속 관계일때 사용하기도 한다.
    jpql: select i from Item i where type(i) = Book;
    기타사항: select m from Member m where m.username is not null
    등과 같이 sql에서 기본 제공하는 >,<,IN,is null, between like 등의 표현은 다 지원한다.
## jpql-case
    1.without exact mathcing when: 
        select case when m.age <= 10 then 'little fare'
                    when m.age >60 then 'elder fare'
                    else 'normal fare'
                end
        from Member m
    2. exact matching when:
            select case t.name 
                when 'teamA' then '110%'
                when 'teamb' then '80%'
                else '0%'
                end
            from Team t
    3.coalesce, nullif
    select coalesce(m.name, "이름이 없는 유저") from Member m -> 이름이 null이면 "이름이 없는 유저" 
    select nullif(m.name, "관리자") from Member m -> 이름이 "관리자" 이면 null 출력
## jpql-기본함수(표준함수)
    1. concat    select concat('a','b'), m.username from Member m
    2. substring select substring(m.username,2,3) from Member m -> username을 2-3 인덱스로 잘라내라
    3. trim
    4. lower,upper
    5. length
    6. locate -> createQuery("select locate("de","abcde") from Member m",Integer.class)
    7. abs,sqrt,mod
    8. size: jpql 콜렉션의 사이즈 반환 select size(t.members) from Team t where t.name='a' -> team 'a'의 멤버수
## jpql-사용자 정의함수
    1. select function('함수이름','인자1') from Member m; 이런식으로 사용한다
    사용하기전 커스텀 Dialect에 등록해주어야한다.
    만일 'group_concat'등의 표준함수를 사용한다면 hibernate는 지원한다. 이때 intellij 컴파일 오류가나면 
    inject 뭐시기로 hql지정해준다.