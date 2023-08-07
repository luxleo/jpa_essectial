package jpashop.domain.superclass;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter @Setter
@MappedSuperclass // 이게 있어야 extends로 다른 entity들이 상속 받는다. 이떄 새로운 entity를 만드는 것은 아님
public class BaseEntity {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
