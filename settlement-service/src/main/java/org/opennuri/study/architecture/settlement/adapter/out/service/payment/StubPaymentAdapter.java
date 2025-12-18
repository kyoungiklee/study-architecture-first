package org.opennuri.study.architecture.settlement.adapter.out.service.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.domain.SettlementItem;
import org.opennuri.study.architecture.settlement.port.out.LoadSettlementTargetsPort;
import org.opennuri.study.architecture.settlement.port.out.UpdatePaymentStatusPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@Slf4j
public class StubPaymentAdapter implements LoadSettlementTargetsPort, UpdatePaymentStatusPort {

    private final Map<String, String> statusStore = new ConcurrentHashMap<>();

    @Override
    public List<SettlementItem> loadTargets() {
        // 로컬 스텁: 임의의 3건 생성
        return List.of(
                SettlementItem.builder()
                        .merchantId("M-1001")
                        .amount(new BigDecimal("10000"))
                        .fee(new BigDecimal("300"))
                        .netAmount(new BigDecimal("9700"))
                        .paymentIds(List.of("P-1", "P-2"))
                        .build(),
                SettlementItem.builder()
                        .merchantId("M-2001")
                        .amount(new BigDecimal("5000"))
                        .fee(new BigDecimal("150"))
                        .netAmount(new BigDecimal("4850"))
                        .paymentIds(List.of("P-3"))
                        .build(),
                SettlementItem.builder()
                        .merchantId("M-3001")
                        .amount(new BigDecimal("7500"))
                        .fee(new BigDecimal("225"))
                        .netAmount(new BigDecimal("7275"))
                        .paymentIds(List.of("P-4", "P-5", "P-6"))
                        .build()
        );
    }

    @Override
    public void markSettled(String paymentId) {
        statusStore.put(paymentId, "SETTLED");
        log.info("[StubPaymentAdapter] paymentId={} marked as SETTLED", paymentId);
    }
}
