# intermidate jpql
## 경로 표현식
    select m.username   ->상태필드
        from Member m
        join m.team t   ->단일값 연관필드
        join m.orders o ->컬렉션값 연관필드 
    where t.name = 'A'
    1. 상태필드 : 엔티티내에서 단순히 값을 저장하기 위한 필드
    2. 연관필드 : 연관관계를 위한 필드
        2-1: 단일 값 연관필드: @ManyToOne ,@OneToOne   : 대상이 엔티티 하나
        2-2: 콜렉션 값 연관필드:@OneToMany, @ManyToMany : 대상이 콜렉션인 경우
## 경로 표현식 조언
    1.묵시적조인(무조건 inner join임)의 한계를 알고, 무조건 명시적 조인을 사용하자
    한계: 단일 값의 경우 조인되어 나가는 쿼리가 불분명해진다.
        컬렉션 타입의 경우 객체 탐색이 불가능하다.
        select t.members.name from Team t -> 불능
        select m.name from Team t 
            join t.members m              -> 가능
    단 콜렉션 타입의 경우 size는 조회가 가능함
        select t.members.size from Team t -> 각 팀의 멤버수 조회 가능

## fetch join
    일반 조인: fetch 전략 보다 우선하지 않는다, 객체그래프를 탐색하지 않는다.
    이 두가지가 합쳐져서 복수의 쿼리가 나간다.
    select m from Member m join m.team 
    => select * from Member m + select * from Team t where m.team_id = t.id
    
    패치 조인: 객체 그래프를 탐색하여 한번의 쿼리로 조인된 테이블,엔티티도 가져온다
    select m from Member m join fetch m.team
    => select m.*, t.* from Member m join Team t on m.team_id = t.id
    
    oneToMany에서의 조회 뻥튀기 막기 -> distinct 사용, hibernate6부터는 그냥 지원한다.
    select distinct m from Member m join fetch m.team
## fetch join 의 특징과 한계
### 한계 - fetch join은 해당 엔티티의 '모든' 정보를 가져온다.
    1. join fetch는 기본적으로 그래프 전체를 가져온다(일대다의 경우 일에 속하는 다 모두를 가져온다.
        t.members -> team의 모든 member)따라서 별칭을 주는 것은 위험하다(cascade등의 옵션으로 인해 
        만일, 별칭으로 조건을 준다면 [select t from Team t join fetch t.members m where m.age>10]
        조건에 필터링된 나머지 데이터가 어떤 영향을 받을지 모른다.
    2. 1에 의하여 별칭을 주지 말자.(where등으롤 필터링 할 가능성이 있다.)
        단, 계속해서 그래프 탐색을 하는 경우 유용한 경우도 있다. 그러나 가급적 사용하면 안된다.
    3. paging이 정상 동작 하지 않는다.
        정확히는 해당 엔티티(일대다 에서 '일'의경우)의 모든 콜렉션을 퍼온후 메모리 위에서 페이징 작업을 한다
        -> 이는 어플리케이션 메모리 자원을 무지 하게 사용하여 뻗어 버리는 케이스가 발생할 수도...
        단, 다대일, 일대일 등 단일 값 연관필드는 가능하다. -> 애초에 필터링 할 수 있으므로
        em.query(select t from Team t) 
            .setFirst(1)
            .setMax(100) -> 하이버네이트는 warn로그 출력후 메모리에서 페이징 처리
        em.query(select m from Member m join fetch m.team)
            .setFirst(1)
            .setMax(100) -> 가능하다.
        단, 일대다의 경우에 @BatchSize를 이용하여 IN절로 한번에 가져와 1+N문제를 막을 수도...
    4. 둘 이상의 컬렉션을 조회할 수 없다.
        가뜩이나 뻥튀기 되는 기질이 있는데(distinct를 사용하지 않으면) 1:N:M의 페치조인을 하면
        뻥튀기 사이즈가 1*N*M이 된다...
### 특징
    1.엔티티에 직접 적용하는 모든 fetch전략 보다 앞선다. -> 근본적으로 1+N의 문제에 유리하다.
    2.그래프를 모두 조회한다. -> 페이징, 조건 필터링이 어렵다(이상하게 동작할 수있다.)
    3.실무의 모든 fetch전략은 LAZY로
    4. 성능 향상이 필요할때 join fetch로 
### 다형성 쿼리
    1. 상속관계 엔티티에 적용한다 by Type(),treat()
    2. Item <= Album,Book,Movie의 상속 관계를 가질때
    jpql: select i from Item i
            where type(i) in (Book,Movie)
    sql: select i from Item
        where i.dtype in ('B','M')
    
    jpql: select i from Item
            where treat(i as Book).author = "Kim"
    sql: select i from Item
            where i.dtype = 'B' and i.author = 'Kim'
## named query - 그런데 spring data jpa를 곁들인
    1.형식
    @Entity
    @NamedQuery(name="Car.findByCarName", query = "select c from Car c where c.name= :name)
    public class Car{...}
    2. 사용: 
    em.createNamedQuery("Car.findByCarName)
    .setParameter("name","porche")
    3. 장점: 컴파일 시점보다 살짝느린 app실행시에 오류를 보여준다 + jpql->sql한번만 변환후 캐싱한다.
    -> jpql -> sql 처리 코스트,오버헤드가 줄어든다.
    4. 그런데 이제는 spring data jpa를 곁들인: -> 이름없는 NamedQuery
    @Query("select c from Car c where c.name = :name)
    Car findByCarName(String name); 쩐다.
# 벌크 쿼리
    특징: [벌크 쿼리 특징]: 영구 컨텍스트를 거치지 않고 바로 디비로 히뜨, 따라서 바로 em.clear()해주어야한다
    문법:         
        query.append("update Car c ");
        query.append("set c.price = c.price*1.1 ");
        query.append("where c.price <20000");
        int i = em.createQuery(query.toString())
                .executeUpdate(); => 영향 받은 로우수 반환


    
    