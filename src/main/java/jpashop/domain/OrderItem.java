package jpashop.domain;

import jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ITEM_Id")
    private Item item;
    private int orderPrice;
    private int count;
}
