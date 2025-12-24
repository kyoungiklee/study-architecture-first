package org.opennuri.study.architecture.membership.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

/**
 * 멤버십 정보 수정을 위한 커맨드 객체입니다.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateMembershipCommand extends SelfValidating<UpdateMembershipCommand> {
    /**
     * 수정할 멤버십의 ID
     */
    @NotBlank
    private String membershipId;

    /**
     * 멤버의 이름
     */
    @NotBlank
    private String name;

    /**
     * 멤버의 이메일 주소
     */
    @NotBlank
    @Email
    private String email;

    /**
     * 멤버의 주소
     */
    @NotBlank
    private String address;

    /**
     * 법인 여부
     */
    private boolean isCorp;

    /**
     * UpdateMembershipCommand 생성자
     *
     * @param membershipId 멤버십 ID
     * @param name 이름
     * @param email 이메일
     * @param address 주소
     * @param isCorp 법인 여부
     */
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
