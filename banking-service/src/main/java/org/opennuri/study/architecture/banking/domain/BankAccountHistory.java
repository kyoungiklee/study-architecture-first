package org.opennuri.study.architecture.banking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 은행 계좌의 변경 이력을 관리하는 도메인 모델입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountHistory {
    /**
     * 이력의 고유 식별자
     */
    private Long id;
    /**
     * 관련 은행 계좌의 식별자
     */
    private Long registeredBankAccountId;
    /**
     * 수행된 작업의 종류 (예: REGISTERED, UPDATED, DELETED 등)
     */
    private String action; // REGISTERED, UPDATED, DELETED 등
}
