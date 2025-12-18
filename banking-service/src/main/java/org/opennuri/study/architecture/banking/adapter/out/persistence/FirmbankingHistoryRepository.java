package org.opennuri.study.architecture.banking.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FirmbankingHistoryRepository extends JpaRepository<FirmbankingHistoryEntity, Long> {
    List<FirmbankingHistoryEntity> findByFirmbankingId(Long firmbankingId);
    List<FirmbankingHistoryEntity> findByFirmbankingIdOrderByCreatedAtDesc(Long firmbankingId);
}
