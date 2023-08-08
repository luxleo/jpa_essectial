package jpashop.domain;

import jpashop.domain.superclass.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@Entity
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "MEMBER_ID")
    private Long id;
    @Column(name = "USERNAME")
    private String username;
    // 아래 코드는 객체 지향적 관점에서 벗어났다. => 객체 설계가 테이블 연관관계에 의존
//    @Column(name = "TEAM_ID")
//    private Long teamId;

    // 아래 방식은 테이블 연관관계(외래키-PK)가 객체설계를 따른다.
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // FetchType.LAZY =>
    @JoinColumn(name = "TEAM_ID") // Member테이블의 TEAM_ID(foreign key)
    private Team team;
    public void setTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
