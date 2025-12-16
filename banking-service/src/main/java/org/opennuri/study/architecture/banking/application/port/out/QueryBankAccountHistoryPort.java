package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.BankAccountHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueryBankAccountHistoryPort {
    Optional<BankAccountHistory> findHistoryById(Long id);
    List<BankAccountHistory> search(Long bankAccountId,
                                    String action,
                                    LocalDateTime from,
                                    LocalDateTime to);
}
