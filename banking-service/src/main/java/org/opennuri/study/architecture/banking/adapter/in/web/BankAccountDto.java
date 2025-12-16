package org.opennuri.study.architecture.banking.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountDto {
    private Long id;
    @NotNull
    private Long memberId;
    @NotBlank
    private String bankCode;
    @NotBlank
    private String bankAccountNo;
    @NotNull
    private Boolean valid;

    public static BankAccountDto from(RegisteredBankAccount domain) {
        return BankAccountDto.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .bankCode(domain.getBankCode())
                .bankAccountNo(domain.getBankAccountNo())
                .valid(domain.isValid())
                .build();
    }
}
