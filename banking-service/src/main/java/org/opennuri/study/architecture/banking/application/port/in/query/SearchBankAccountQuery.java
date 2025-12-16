package org.opennuri.study.architecture.banking.application.port.in.query;

import org.opennuri.study.architecture.banking.domain.BankAccount;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchBankAccountQuery {
    List<BankAccount> search(Long memberId,
                             String bankCode,
                             String bankAccountNo,
                             Boolean valid,
                             LocalDateTime createdFrom,
                             LocalDateTime createdTo);
}
