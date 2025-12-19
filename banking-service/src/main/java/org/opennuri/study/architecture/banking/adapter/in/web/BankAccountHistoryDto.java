package org.opennuri.study.architecture.banking.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.BankAccountHistory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "은행 계좌 변경 이력 DTO")
public class BankAccountHistoryDto {
    @Schema(description = "이력 ID", example = "1")
    private Long id;

    @Schema(description = "은행 계좌 ID", example = "1")
    private Long bankAccountId;

    @Schema(description = "수행 작업 (REGISTER, UPDATE, DELETE)", example = "REGISTER")
    private String action;

    public static BankAccountHistoryDto from(BankAccountHistory domain) {
        return BankAccountHistoryDto.builder()
                .id(domain.getId())
                .bankAccountId(domain.getRegisteredBankAccountId())
                .action(domain.getAction())
                .build();
    }
}
