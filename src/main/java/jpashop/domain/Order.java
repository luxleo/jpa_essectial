package jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "ORDERS")
public class Order {
    @Id @GeneratedValue // 기본이 auto 이다.
    @Column(name = "ORDER_ID")
    private Long id;
    //    @Column(name = "MEMBER_ID")
//    private Long memberId;
    @ManyToOne // 단방향 만으로 테이블 설계는 끝이난다는것 명심하자
    @JoinColumn(name = "MEMBER_ID") // FK설정
    private Member member;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING) // ordinal 안된다.
    private OrderStatus status;
}
