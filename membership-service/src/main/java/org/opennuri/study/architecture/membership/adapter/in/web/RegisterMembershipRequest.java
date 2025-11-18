package org.opennuri.study.architecture.membership.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMembershipRequest {
    // Membership 정보를 클라이언트로부터 받기 위한 클래스
    // 멤버 등록 시 필요한 기본 정보만 수신합니다.

    /**
     * 멤버 이름(개인/법인명)
     */
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    /**
     * 이메일 주소
     */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    /**
     * 주소
     */
    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    /**
     * 법인 여부 (기본값: false)
     */
    private boolean isCorp;
}
