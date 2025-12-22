package org.opennuri.study.architecture.banking.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

public interface BankAccountHistoryRepositoryCustom {
    List<BankAccountHistoryEntity> search(Long bankAccountId,
                                          String action,
                                          LocalDateTime from,
                                          LocalDateTime to);
}
