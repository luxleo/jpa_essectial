package jpashop.executor;

import jpashop.domain.Member;
import jpashop.domain.Team;
import jpashop.domain.cascade.Child;
import jpashop.domain.cascade.Parent;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class ProxyExec {
    private final EntityManager em;

    public void typeCheck() {
        System.out.println("============[TYPECHECKTEST]============");
        Member m1 = new Member();
        m1.setUsername("binzino");
        em.persist(m1);
        em.flush();
        em.clear();

        Member refMember = em.getReference(Member.class, m1.getId());
        System.out.println("refMember.class = "+refMember.getClass());

        Member findMember = em.find(Member.class, m1.getId());
        System.out.println("entity == proxy? " +( refMember.getClass() == findMember.getClass()));

        System.out.println("============[TYPECHECKTEST]============");
    }

    public void lazyAndEagerCheck() {
        System.out.println("============[Lazy and Eager]============");
        Member member = new Member();
        Team team = new Team();
        team.setName("팀 드래곤");

        member.setUsername("빈지노");
        member.setTeam(team);
        em.persist(member);
        em.flush();
        em.clear();
        System.out.println("----before hit Team in LAZY LOADING-----");
        Member findMember = em.find(Member.class, member.getId());
        System.out.println("팀은 프록시이다. "+findMember.getTeam().getClass());

        System.out.println("\n----after hit Team in LAZY LOADING-----");
        findMember.getTeam().getName();
        System.out.println("팀은 엔티티이다. "+findMember.getTeam().getClass());
    }

    /**
     * 연관 매핑된 엔티티사이에서 한 엔티티에 다른 엔티티의 영속성 컨텍스트의 기능을 위임한다.
     * em.persist, em.remove의 대상이 된 엔티티가
     * 연관 매핑된 다른 엔티티에도 같은 영향을 미친다.
     */
    public void CascadeAndOrphanObjectCheck() {
        System.out.println("============[cascade1]============");
        Member member = new Member();
        member.setUsername("빈지노");

        Team team = new Team();
        team.setName("zinoTeam");

        member.setTeam(team);
        em.persist(member);
        em.flush();

        System.out.println("many to one 연관관계의 주인이더라도 cascade 가능하다");
        System.out.println("멤버만 저장했지만 팀도 돼었다구 team id = "+member.getTeam().getId());

        System.out.println("마찬가지로 멤버를 삭제하면 팀도 삭제가 된다고! 쿼리를 확인하라");
        em.remove(member);
    }

    public void CascadeAndOrphanObjectCheckV2() {
        System.out.println("============[cascade2]============");
        Parent parent = new Parent();
        parent.setName("p1");

        for (int i = 0; i < 3; i++) {
            Child child = new Child();
            child.setName(String.format("%dth child", i));
            parent.addChild(child);
        }
        System.out.println("부모만 persist하여도 @OneToMany의 cascade에 의해 child도 저장\n");
        em.persist(parent);
        em.flush();
        em.clear();

        Parent findParent = em.find(Parent.class, parent.getId());
        Child child = findParent.getChildren().get(0);
        System.out.println("부모의 리스트에서 삭제하면 자식은 'orphanRemoval=true' 에 의하여 제거된다.");
        System.out.println("[children.remove()전]: child.getName() = " + child.getName());

        System.out.println("\n[children.remove()전]: 쿼리를 확인하세용");
        findParent.getChildren().remove(0);

        System.out.println("\n[parent.remove되면 모두가 날라간다.]");
        em.remove(findParent);
    }
    public void CascadeAndOrphanObjectCheckV3() {
        Parent parent = new Parent();
        for (int i = 0; i < 3; i++) {
            Child child = new Child();
            child.setName(String.format("%dth child", i));
            parent.addChild(child);
        }
        em.persist(parent);
        em.flush();
        em.clear();
        System.out.println("[Child]의 parent속성의 @ManyToOne(fetch=Fetch.EAGER)로 지정한다, 이후 child를 조회하면 2번의 쿼리가 나간다.");
        Child child = em.find(Child.class, 1L);
    }
}
