package org.opennuri.study.architecture.settlement.adapter.out.service.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.opennuri.study.architecture.settlement.domain.PaymentData;
import org.opennuri.study.architecture.settlement.port.out.LoadPaymentDataPort;
import org.opennuri.study.architecture.settlement.port.out.UpdatePaymentStatusPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("local")
@Slf4j
public class StubPaymentAdapter implements UpdatePaymentStatusPort, LoadPaymentDataPort {

    private final Map<String, String> statusStore = new ConcurrentHashMap<>();

    @Override
    public List<PaymentData> loadCompletedPayments() {
        return List.of(
                PaymentData.builder().paymentId("P-1").merchantId("M-1001").amount(new BigDecimal("5000")).paymentDateTime(LocalDateTime.now()).build(),
                PaymentData.builder().paymentId("P-2").merchantId("M-1001").amount(new BigDecimal("5000")).paymentDateTime(LocalDateTime.now()).build(),
                PaymentData.builder().paymentId("P-3").merchantId("M-2001").amount(new BigDecimal("5000")).paymentDateTime(LocalDateTime.now()).build(),
                PaymentData.builder().paymentId("P-4").merchantId("M-3001").amount(new BigDecimal("2500")).paymentDateTime(LocalDateTime.now()).build(),
                PaymentData.builder().paymentId("P-5").merchantId("M-3001").amount(new BigDecimal("2500")).paymentDateTime(LocalDateTime.now()).build(),
                PaymentData.builder().paymentId("P-6").merchantId("M-3001").amount(new BigDecimal("2500")).paymentDateTime(LocalDateTime.now()).build()
        );
    }

    @Override
    public void markSettled(String paymentId) {
        statusStore.put(paymentId, "SETTLED");
        log.info("[StubPaymentAdapter] paymentId={} marked as SETTLED", paymentId);
    }

}
