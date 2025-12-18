package org.opennuri.study.architecture.money.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.money.application.port.in.GetMemberMoneyUseCase;
import org.opennuri.study.architecture.money.application.port.in.GetMoneyChangingHistoryUseCase;
import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class MoneyQueryController {

    private final GetMemberMoneyUseCase getMemberMoneyUseCase;
    private final GetMoneyChangingHistoryUseCase getMoneyChangingHistoryUseCase;

    @GetMapping(path = "/money/{membershipId}")
    Long getMemberMoney(@PathVariable String membershipId) {
        return getMemberMoneyUseCase.getMemberMoney(membershipId);
    }

    @GetMapping(path = "/money/{membershipId}/history")
    List<MoneyChangingHistory> getHistory(@PathVariable String membershipId) {
        return getMoneyChangingHistoryUseCase.getHistory(membershipId);
    }
}
