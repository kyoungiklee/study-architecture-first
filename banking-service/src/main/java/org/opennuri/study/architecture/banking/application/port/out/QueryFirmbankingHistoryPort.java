package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.FirmbankingHistory;

import java.util.List;

/**
 * 펌뱅킹 이력 조회 포트
 */
public interface QueryFirmbankingHistoryPort {
    List<FirmbankingHistory> findByFirmbankingId(Long firmbankingId);
}
