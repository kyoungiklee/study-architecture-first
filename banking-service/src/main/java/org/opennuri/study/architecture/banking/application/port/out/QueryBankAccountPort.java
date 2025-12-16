package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueryBankAccountPort {
    Optional<RegisteredBankAccount> findById(Long id);
    List<RegisteredBankAccount> search(Long memberId,
                                       String bankCode,
                                       String bankAccountNo,
                                       Boolean valid,
                                       LocalDateTime createdFrom,
                                       LocalDateTime createdTo);
}
