package org.opennuri.study.architecture.money.application.port.out;

import org.opennuri.study.architecture.money.domain.Money;

/**
 * 머니 정보를 저장하기 위한 Outbound Port 인터페이스입니다.
 */
public interface SaveMoneyPort {
    /**
     * 머니 정보를 저장합니다.
     *
     * @param money 머니 도메인 모델
     */
    void saveMoney(Money money);
}
