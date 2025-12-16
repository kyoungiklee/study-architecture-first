package org.opennuri.study.architecture.banking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFirmbanking {
    private Long id;
    private String fromBankAccount;
    private String toBankAccount;
    private BigDecimal amount;
    private String status; // REQUESTED, APPROVED, REJECTED, COMPLETED 등
}
