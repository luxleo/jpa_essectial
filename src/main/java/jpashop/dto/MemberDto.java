package jpashop.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberDto {
    private Long id;
    private String username;

    @Builder
    public MemberDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
