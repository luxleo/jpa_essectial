package jpashop.domain.car;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter @Setter
@Entity
@DiscriminatorValue("O_CAR")
@NoArgsConstructor
public class OilCar extends Car{
    private String engine;
    private int efficiency;
    @Builder
    public OilCar(String engine, int efficiency,int price,String name,int power) {
        this.engine = engine;
        this.efficiency = efficiency;
        setPower(power);
        setPrice(price);
        setName(name);
    }
}
