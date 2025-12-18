package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.FirmbankingHistory;

/**
 * 펌뱅킹 이력 명령 포트
 */
public interface CommandFirmbankingHistoryPort {
    void saveHistory(FirmbankingHistory history);
}
