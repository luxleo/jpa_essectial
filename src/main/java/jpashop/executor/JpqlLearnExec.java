package jpashop.executor;

import jpashop.domain.Member;
import jpashop.domain.Team;
import jpashop.domain.car.Car;
import jpashop.domain.car.OilCar;
import jpashop.domain.valuetype.People;
import jpashop.dto.MemberDto;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class JpqlLearnExec {
    private final EntityManager em;

    public void projection_test() {
        System.out.println("========[projection test]========");
        Member member = new Member();
        member.setUsername("binzino");
        em.persist(member);
        em.flush();
        em.clear();

        System.out.println("Query 타입 쿼리를 받아오는 방법1. Object[]");
        List<Object[]> res = em.createQuery("select m.id ,m.username from Member m")
                .getResultList();
        for (Object[] row : res) {
            System.out.println(row[0]+ " "+row[1]);
        }
        em.clear();

        System.out.println("Query 타입 쿼리를 받아오는 방법2. dto");
        List<MemberDto> resultList = em.createQuery("select new jpashop.dto.MemberDto(m.id,m.username) from Member m",
                        MemberDto.class)
                .getResultList();
        for (MemberDto memberDto : resultList) {
            System.out.println("memberDto.getId() = " + memberDto.getId());
            System.out.println("memberDto.getUsername() = " + memberDto.getUsername());
        }
    }

    public void paging_test() {
        System.out.println("========[paging test]========");
        for (int i = 0; i < 11; i++) {
            People people = new People();
            people.setName(String.format("%dth people",i));
            em.persist(people);
        }
        em.flush();
        em.clear();

        System.out.println("1-10까지 페이징");
        List<People> resultList = em.createQuery(
                "select m from People m order by m.id",
                People.class
        )
                .setFirstResult(1)
                .setMaxResults(10)
                .getResultList();
        String findName = resultList.get(0).getName();
        System.out.println("findName = " + findName);
    }

    public void graph_search_test() {
        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        Member member4 = new Member();

        member1.setUsername("dragon1");
        member2.setUsername("dragon2");
        member3.setUsername("dragon3");
        member4.setUsername("dragon4");

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        Team team1 = new Team();
        Team team2 = new Team();

        team1.setName("A Team");
        team2.setName("B Team");

        team1.addMember(member1);
        team1.addMember(member2);
        team1.addMember(member3);

        team2.addMember(member4);

        em.persist(team1);
        em.persist(team2);
        em.flush();

        System.out.println("\n[단일값 연관 필드]의 경우 묵시적 조인시 쿼리가 조인되어 나간다.");
        System.out.println("[묵시적 조인 테스트-단일값] select m.team from Member m");
        List<String> resultList = em.createQuery(
                "select m.team.name from Member m",String.class
        ).getResultList();
        for (String s : resultList) {
            System.out.println("team Name = " + s);
        }

        System.out.println("\n[컬렉션 연관 필드]묵시적 조인시 쿼리가 나가지 않는다. 객체탐색 불능");
        System.out.println("[묵시적 조인 테스트-콜랙션] select t.members from Team t");
        List resultList1 = em.createQuery(
                "select t.members.size from Team t"
        ).getResultList();
        for (Object o : resultList1) {
            System.out.println("o = " + o);
        }

        System.out.println("\n[무조건 명시적 조인 사용하자!!]어떤 쿼리가 조인이되는지, 어떻게 되는지 파악이 편리해진다.");
        List<String> resultList2 = em.createQuery(
                "select m.team.name from Member m " +
                        "join m.team t", String.class
        ).getResultList();
        for (String s : resultList2) {
            System.out.println("team_name = " + s);
        }

        System.out.println("\n[컬렉션 타입의 경우 명시적 조인이 있어야 엔티티 그래프 탐색이 가능하다.]");
        List<String> resultList3 = em.createQuery(
                "select m.username from Team t "
                        + "join t.members m", String.class
        ).getResultList();
        for (String s : resultList3) {
            System.out.println("member_name = " + s);
        }
    }

    public void bulk_query_test() {
        OilCar c1 = OilCar.builder()
                .price(10000)
                .name("c1")
                .build();
        OilCar c2 = OilCar.builder()
                .price(10000)
                .name("c1")
                .build();
        OilCar c3 = OilCar.builder()
                .price(30000)
                .name("c1")
                .build();
        em.persist(c1);
        em.persist(c2);
        em.persist(c3);

        StringBuilder query = new StringBuilder();
        query.append("update Car c ");
        query.append("set c.price = c.price*1.1 ");
        query.append("where c.price <20000");

        System.out.println("\n[벌크 쿼리 테스트]jpql: update Car c set c.price = c.price*1.1 where c.price <20000");

        int i = em.createQuery(query.toString())
                .executeUpdate();
        System.out.println("updated_row_num = " + i);

        System.out.println("\n[벌크 쿼리 특징]: 영구 컨텍스트를 거치지 않고 바로 디비로 히뜨, 따라서 바로 em.clear()해주어야한다.");
        em.clear();

        query.setLength(0);
        query.append("select c From Car c ");
        List<Car> resultList = em.createQuery(query.toString(), Car.class)
                .getResultList();
        for (Car car : resultList) {
            System.out.println("\ncar.getName() = " + car.getName());
            System.out.println("car.getPrice() = " + car.getPrice());
        }

    }
}
