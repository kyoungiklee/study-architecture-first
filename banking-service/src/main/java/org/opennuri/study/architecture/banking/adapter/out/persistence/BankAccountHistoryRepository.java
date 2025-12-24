package org.opennuri.study.architecture.banking.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountHistoryRepository extends JpaRepository<BankAccountHistoryEntity, Long>, BankAccountHistoryRepositoryCustom {
}
