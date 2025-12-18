package org.opennuri.study.architecture.membership.application.port.in;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterMembershipCommand extends SelfValidating<RegisterMembershipCommand> {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String address;

    private boolean isCorp;

    // 모든 정보가 유효한지 여부 (Bean Validation 통과 시 true)
    private boolean isValid;

    @Builder
    public RegisterMembershipCommand(String name, String email, String address, boolean isCorp) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.isCorp = isCorp;
        // 초기값: 아직 검증 전이므로 false
        this.isValid = false;

        // Bean Validation 규칙을 즉시 검증
        validateSelf();

        // 예외가 발생하지 않았다면 모든 정보가 유효하다는 의미이므로 true로 설정
        this.isValid = true;
    }
}
