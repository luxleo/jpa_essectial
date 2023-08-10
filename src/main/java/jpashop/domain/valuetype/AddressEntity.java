package jpashop.domain.valuetype;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "ADDRESS_ENTITY")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ADDRESS_ID")
    private Long id;
    private Address address;

    public AddressEntity(String city, String street, String zipcode) {
        address = new Address(city, street, zipcode);
    }

}
