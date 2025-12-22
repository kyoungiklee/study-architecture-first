package org.opennuri.study.architecture.settlement.adapter.out.service.banking;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.port.out.RequestFirmbankingPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@Slf4j
public class StubBankingAdapter implements RequestFirmbankingPort {
    @Override
    public boolean requestTransfer(String merchantId, BigDecimal amount) {
        log.info("[StubBankingAdapter] Transfer requested merchantId={}, amount={}", merchantId, amount);
        return true;
    }
}
