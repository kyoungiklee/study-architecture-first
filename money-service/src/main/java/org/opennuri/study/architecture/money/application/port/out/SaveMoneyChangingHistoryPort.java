package org.opennuri.study.architecture.money.application.port.out;

import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;

/**
 * 머니 변동 이력을 저장하기 위한 Outbound Port 인터페이스입니다.
 */
public interface SaveMoneyChangingHistoryPort {
    /**
     * 머니 변동 이력을 저장합니다.
     *
     * @param history 머니 변동 이력 도메인 모델
     */
    void saveMoneyChangingHistory(MoneyChangingHistory history);
}
