package org.opennuri.study.architecture.money.application.port.out;

import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;

public interface SaveMoneyChangingHistoryPort {
    void saveMoneyChangingHistory(MoneyChangingHistory history);
}
