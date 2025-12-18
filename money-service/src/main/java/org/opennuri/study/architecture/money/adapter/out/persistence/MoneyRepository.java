package org.opennuri.study.architecture.money.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyRepository extends JpaRepository<MoneyEntity, Long> {
    Optional<MoneyEntity> findByMembershipId(String membershipId);
}
