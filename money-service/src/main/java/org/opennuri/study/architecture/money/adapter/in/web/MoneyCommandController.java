package org.opennuri.study.architecture.money.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.money.application.port.in.RequestMoneyChangingUseCase;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class MoneyCommandController {

    private final RequestMoneyChangingUseCase requestMoneyChangingUseCase;

    @PostMapping(path = "/money/increase")
    void increaseMoneyChangingRequest(@RequestBody MoneyChangingRequest request) {
        RequestMoneyChangingUseCase.IncreaseMoneyRequest command = RequestMoneyChangingUseCase.IncreaseMoneyRequest.builder()
                .targetMembershipId(request.getTargetMembershipId())
                .amount(request.getAmount())
                .build();
        requestMoneyChangingUseCase.increaseMoney(command);
    }

    @PostMapping(path = "/money/decrease")
    void decreaseMoneyChangingRequest(@RequestBody MoneyChangingRequest request) {
        RequestMoneyChangingUseCase.DecreaseMoneyRequest command = RequestMoneyChangingUseCase.DecreaseMoneyRequest.builder()
                .targetMembershipId(request.getTargetMembershipId())
                .amount(request.getAmount())
                .build();
        requestMoneyChangingUseCase.decreaseMoney(command);
    }
}
