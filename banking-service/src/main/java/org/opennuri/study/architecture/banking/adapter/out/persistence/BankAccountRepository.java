package org.opennuri.study.architecture.banking.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 은행 계좌 엔티티에 대한 기본 CRUD 기능을 제공하는 리포지토리입니다.
 */
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long>, BankAccountRepositoryCustom {
}
