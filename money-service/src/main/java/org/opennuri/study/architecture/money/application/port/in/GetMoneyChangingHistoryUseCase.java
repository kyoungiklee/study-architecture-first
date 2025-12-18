package org.opennuri.study.architecture.money.application.port.in;

import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;

import java.util.List;

public interface GetMoneyChangingHistoryUseCase {
    List<MoneyChangingHistory> getHistory(String membershipId);
}
