package org.opennuri.study.architecture.banking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 은행 계좌 도메인 모델입니다.
 * 헥사고날 아키텍처의 도메인 계층에 위치하며 비즈니스 로직의 핵심 엔티티입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    /**
     * 계좌의 고유 식별자
     */
    private Long id;
    /**
     * 계좌 소유자의 회원 ID
     */
    private Long memberId;
    /**
     * 은행 코드 (예: 001, 002 등)
     */
    private String bankCode;
    /**
     * 계좌 번호
     */
    private String bankAccountNo;
    /**
     * 계좌의 유효 상태 여부
     */
    private boolean valid;
}
