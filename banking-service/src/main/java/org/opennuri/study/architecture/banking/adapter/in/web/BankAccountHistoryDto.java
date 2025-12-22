package org.opennuri.study.architecture.banking.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.BankAccountHistory;

/**
 * 은행 계좌 변경 이력 정보를 전달하기 위한 데이터 전송 객체(DTO)입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "은행 계좌 변경 이력 DTO")
public class BankAccountHistoryDto {
    /**
     * 이력 고유 ID
     */
    @Schema(description = "이력 ID", example = "1")
    private Long id;

    /**
     * 관련 은행 계좌 ID
     */
    @Schema(description = "은행 계좌 ID", example = "1")
    private Long bankAccountId;

    /**
     * 수행된 작업 종류
     */
    @Schema(description = "수행 작업 (REGISTER, UPDATE, DELETE)", example = "REGISTER")
    private String action;

    /**
     * 도메인 모델을 DTO로 변환합니다.
     *
     * @param domain 계좌 이력 도메인 모델
     * @return 변환된 DTO 객체
     */
    public static BankAccountHistoryDto from(BankAccountHistory domain) {
        return BankAccountHistoryDto.builder()
                .id(domain.getId())
                .bankAccountId(domain.getRegisteredBankAccountId())
                .action(domain.getAction())
                .build();
    }
}
