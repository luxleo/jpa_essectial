# intermidate jpql
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