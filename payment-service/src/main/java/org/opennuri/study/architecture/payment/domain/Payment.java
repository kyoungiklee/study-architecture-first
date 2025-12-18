package org.opennuri.study.architecture.payment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    @Getter private final Long paymentId;
    @Getter private final Long memberId;
    @Getter private final Long amount;
    @Getter private final String merchantId;
    @Getter private final PaymentStatus status;
    @Getter private final LocalDateTime createdAt;

    public static Payment withId(
            Long paymentId,
            Long memberId,
            Long amount,
            String merchantId,
            PaymentStatus status,
            LocalDateTime createdAt) {
        return new Payment(paymentId, memberId, amount, merchantId, status, createdAt);
    }

    public enum PaymentStatus {
        REQUESTED,
        APPROVED,
        CANCELED,
        FAILED
    }

    @Value
    public static class PaymentId {
        Long value;
    }
}
