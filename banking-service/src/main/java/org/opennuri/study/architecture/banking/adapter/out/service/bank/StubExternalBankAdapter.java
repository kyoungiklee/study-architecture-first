package org.opennuri.study.architecture.banking.adapter.out.service.bank;

import org.opennuri.study.architecture.banking.application.port.out.ExternalBankPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Profile("local")
@Component
public class StubExternalBankAdapter implements ExternalBankPort {
    @Override
    public boolean transfer(String fromBankAccount, String toBankAccount, BigDecimal amount) {
        // local 프로필에서 항상 성공으로 가정하는 더미 구현
        return true;
    }
}
