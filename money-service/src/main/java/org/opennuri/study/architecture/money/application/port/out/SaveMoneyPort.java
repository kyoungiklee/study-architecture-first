package org.opennuri.study.architecture.money.application.port.out;

import org.opennuri.study.architecture.money.domain.Money;

public interface SaveMoneyPort {
    void saveMoney(Money money);
}
