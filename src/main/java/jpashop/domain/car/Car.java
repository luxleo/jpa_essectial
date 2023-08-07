package jpashop.domain.car;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn // InheritanceType.JOINED의 경우 기본적으로 dtype생성하지 않는다.
public class Car {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int price;
    private int power;
}
