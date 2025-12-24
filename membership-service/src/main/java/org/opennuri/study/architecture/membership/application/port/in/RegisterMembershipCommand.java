package org.opennuri.study.architecture.membership.application.port.in;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

/**
 * 멤버십 등록 요청 데이터를 담는 커맨드 객체입니다.
 * 입력 데이터의 유효성을 스스로 검증하는 책임을 가집니다.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterMembershipCommand extends SelfValidating<RegisterMembershipCommand> {
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
     * 데이터의 유효성 상태
     */
    private boolean isValid;

    /**
     * RegisterMembershipCommand 생성자
     * 생성 시점에 Bean Validation을 이용해 데이터의 유효성을 검증합니다.
     *
     * @param name 이름
     * @param email 이메일
     * @param address 주소
     * @param isCorp 법인 여부
     */
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
