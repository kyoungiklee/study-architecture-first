package org.opennuri.study.architecture.banking.adapter.in.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.RegisteredBankAccountHistory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountHistoryDto {
    private Long id;
    private Long bankAccountId;
    private String action;

    public static BankAccountHistoryDto from(RegisteredBankAccountHistory domain) {
        return BankAccountHistoryDto.builder()
                .id(domain.getId())
                .bankAccountId(domain.getRegisteredBankAccountId())
                .action(domain.getAction())
                .build();
    }
}
