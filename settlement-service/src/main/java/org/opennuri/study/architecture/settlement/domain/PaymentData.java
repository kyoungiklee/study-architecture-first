package org.opennuri.study.architecture.settlement.domain;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class PaymentData {
    String paymentId;
    String merchantId;
    BigDecimal amount;
    LocalDateTime paymentDateTime;
}
