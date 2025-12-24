package org.opennuri.study.architecture.banking.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FirmbankingRepository extends JpaRepository<FirmbankingEntity, Long> {
    List<FirmbankingEntity> findByMemberId(Long memberId);
    List<FirmbankingEntity> findByStatus(FirmbankingEntity.FirmbankingStatusEntity status);
}
