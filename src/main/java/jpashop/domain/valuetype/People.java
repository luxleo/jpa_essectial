package jpashop.domain.valuetype;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
@Entity
public class People {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PEOPLE_ID")
    private Long id;
    private String name;
    @Embedded
    private Address homeAdress;
    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOOD", joinColumns =
    @JoinColumn(name = "PEOPLE_ID"))
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    /*
    이 녀석은 문제가 많다. 특히 수정시에는 난리도 아니다.(다 삭제하고 변경된 녀석 포함하여 추가)
     */
    @ElementCollection
    @CollectionTable(name = "ADRESS_HISTORY",joinColumns =
    @JoinColumn(name = "PEOPLE_ID"))
    private List<Address> addressHistory = new ArrayList<>();

    /*
    위 문제를 TypeValue entity로 승격하여 해결하자.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "PEOPLE_ID")
    private List<AddressEntity> addressHistoryV2 = new ArrayList<>();
}
