package jpashop.domain.valuetype;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class FavoriteFood {
    private String foodName;

    public FavoriteFood(String foodName) {
        this.foodName = foodName;
    }
}
