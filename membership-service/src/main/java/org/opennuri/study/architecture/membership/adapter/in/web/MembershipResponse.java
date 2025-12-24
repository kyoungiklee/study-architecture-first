package org.opennuri.study.architecture.membership.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.membership.domain.Membership;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "멤버십 응답")
public class MembershipResponse {
    @Schema(description = "멤버십 ID", example = "M001")
    private String membershipId;
    @Schema(description = "멤버 이름", example = "홍길동")
    private String name;
    @Schema(description = "이메일 주소", example = "hong@example.com")
    private String email;
    @Schema(description = "주소", example = "서울시 강남구")
    private String address;
    @Schema(description = "법인 여부", example = "false")
    private boolean isCorp;
    @Schema(description = "유효 상태", example = "true")
    private boolean isValid;

    public static MembershipResponse from(Membership m) {
        return MembershipResponse.builder()
                .membershipId(m.getMembershipId())
                .name(m.getName())
                .email(m.getEmail())
                .address(m.getAddress())
                .isCorp(m.isCorp())
                .isValid(m.isValid())
                .build();
    }
}
