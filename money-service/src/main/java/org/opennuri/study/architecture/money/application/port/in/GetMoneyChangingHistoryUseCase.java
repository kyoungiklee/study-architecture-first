package org.opennuri.study.architecture.money.application.port.in;

import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;

import java.util.List;

/**
 * 멤버십의 머니 변동 이력을 조회하는 Use Case 인터페이스입니다.
 */
public interface GetMoneyChangingHistoryUseCase {
    /**
     * 특정 멤버십의 머니 변동 이력을 조회합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 변동 이력 목록
     */
    List<MoneyChangingHistory> getHistory(String membershipId);
}
