package jpashop.executor;

import jpashop.domain.Member;
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
}
