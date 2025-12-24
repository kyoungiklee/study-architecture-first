package org.opennuri.study.architecture.banking.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * 은행 계좌 생성을 위한 커맨드 객체입니다.
 */
@Value
@Builder
public class CreateBankAccountCommand {
    /**
     * 계좌 소유자의 회원 ID
     */
    @NotNull Long memberId;
    /**
     * 은행 코드
     */
    @NotBlank String bankCode;
    /**
     * 계좌 번호
     */
    @NotBlank String bankAccountNo;
    /**
     * 계좌의 유효 상태 여부
     */
    @NotNull Boolean valid;
}
