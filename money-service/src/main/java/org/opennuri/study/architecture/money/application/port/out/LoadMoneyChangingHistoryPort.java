package org.opennuri.study.architecture.money.application.port.out;

import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;

import java.util.List;

/**
 * 잔액 변동 이력 조회를 위한 Port
 */
public interface LoadMoneyChangingHistoryPort {
    /**
     * 특정 회원의 잔액 변동 이력 조회
     * @param membershipId 회원 ID
     * @return 잔액 변동 이력 목록
     */
    List<MoneyChangingHistory> loadMoneyChangingHistory(String membershipId);
}
