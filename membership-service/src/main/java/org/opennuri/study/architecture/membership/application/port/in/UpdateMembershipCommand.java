package org.opennuri.study.architecture.membership.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateMembershipCommand extends SelfValidating<UpdateMembershipCommand> {
    @NotBlank
    private String membershipId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String address;

    private boolean isCorp;

    @Builder
    public UpdateMembershipCommand(String membershipId, String name, String email, String address, boolean isCorp) {
        this.membershipId = membershipId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.isCorp = isCorp;

        validateSelf();
    }
}
