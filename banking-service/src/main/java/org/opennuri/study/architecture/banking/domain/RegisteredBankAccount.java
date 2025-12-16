package org.opennuri.study.architecture.banking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredBankAccount {
    private Long id;
    private Long memberId;
    private String bankCode;
    private String bankAccountNo;
}
