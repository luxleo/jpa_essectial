package jpashop.domain.car;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter @Setter
@Entity
@DiscriminatorValue("O_CAR")
public class OilCar extends Car{
    private String engine;
    private int efficiency;
}
