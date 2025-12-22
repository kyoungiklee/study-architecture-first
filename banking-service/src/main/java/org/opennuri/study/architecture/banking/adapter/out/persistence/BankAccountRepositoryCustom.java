package org.opennuri.study.architecture.banking.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

public interface BankAccountRepositoryCustom {
    List<BankAccountEntity> search(Long memberId,
                                   String bankCode,
                                   String bankAccountNo,
                                   Boolean valid,
                                   LocalDateTime createdFrom,
                                   LocalDateTime createdTo);
}
