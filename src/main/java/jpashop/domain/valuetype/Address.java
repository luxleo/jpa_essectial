package jpashop.domain.valuetype;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
public class Address {
    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Address obj1 = (Address) obj;
        return Objects.equals(city, obj1.getCity()) &&
                Objects.equals(street, obj1.getStreet()) &&
                Objects.equals(zipcode, obj1.getZipcode());
    }
}
