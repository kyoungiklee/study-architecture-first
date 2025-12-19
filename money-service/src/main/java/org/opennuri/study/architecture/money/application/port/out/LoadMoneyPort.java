package org.opennuri.study.architecture.money.application.port.out;

import org.opennuri.study.architecture.money.domain.Money;

/**
 * 머니 정보를 로드하기 위한 Outbound Port 인터페이스입니다.
 */
public interface LoadMoneyPort {
    /**
     * 특정 멤버십의 머니 정보를 로드합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 도메인 모델
     */
    Money loadMoney(String membershipId);
}
