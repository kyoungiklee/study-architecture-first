package org.opennuri.study.architecture.membership.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.membership.domain.Membership;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private String membershipId;
    private String name;
    private String email;
    private String address;
    private boolean isCorp;
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
