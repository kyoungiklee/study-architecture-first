package org.opennuri.study.architecture.payment.adapter.out.service.money;

import org.opennuri.study.architecture.payment.application.port.out.CreditMoneyPort;
import org.opennuri.study.architecture.payment.application.port.out.DebitMoneyPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
public class StubMoneyAdapter implements DebitMoneyPort, CreditMoneyPort {

    @Override
    public boolean debitMoney(Long memberId, Long amount) {
        return true;
    }

    @Override
    public boolean creditMoney(Long memberId, Long amount) {
        return true;
    }
}
