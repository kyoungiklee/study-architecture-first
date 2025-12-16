package org.opennuri.study.architecture.banking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredBankAccountHistory {
    private Long id;
    private Long registeredBankAccountId;
    private String action; // REGISTERED, UPDATED, DELETED 등
}
