package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.RegisteredBankAccountHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueryBankAccountHistoryPort {
    Optional<RegisteredBankAccountHistory> findHistoryById(Long id);
    List<RegisteredBankAccountHistory> search(Long bankAccountId,
                                              String action,
                                              LocalDateTime from,
                                              LocalDateTime to);
}
