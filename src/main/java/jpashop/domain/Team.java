package jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "team",fetch = FetchType.LAZY) // mappedBy는 참조되는 녀석(이 경우 Member)의 필드의 이름이다.
    private List<Member> members = new ArrayList<>();
}
