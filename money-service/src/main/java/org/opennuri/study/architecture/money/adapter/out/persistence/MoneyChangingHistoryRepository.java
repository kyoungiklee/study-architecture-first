package org.opennuri.study.architecture.money.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoneyChangingHistoryRepository extends JpaRepository<MoneyChangingHistoryEntity, Long> {
    List<MoneyChangingHistoryEntity> findByMembershipId(String membershipId);
}
