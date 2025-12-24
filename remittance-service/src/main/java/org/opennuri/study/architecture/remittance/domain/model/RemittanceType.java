package org.opennuri.study.architecture.remittance.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 송금 유형
 */
@Schema(description = "송금 유형")
public enum RemittanceType {
    @Schema(description = "내부 회원 송금")
    MEMBERSHIP,
    @Schema(description = "외부 은행 송금")
    BANK_ACCOUNT
}
