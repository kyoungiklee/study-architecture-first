package org.opennuri.study.architecture.settlement.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SettlementDetail {
    private final String settlementDetailId;
    private final String merchantId;
    private BigDecimal grossAmount;
    private BigDecimal feeAmount;
    private BigDecimal netAmount;
    private SettlementStatus status;
    private final List<String> paymentIds;

    public static SettlementDetail create(String merchantId, List<PaymentData> payments) {
        BigDecimal gross = payments.stream()
                .map(PaymentData::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<String> ids = payments.stream()
                .map(PaymentData::getPaymentId)
                .toList();

        return SettlementDetail.builder()
                .settlementDetailId(java.util.UUID.randomUUID().toString())
                .merchantId(merchantId)
                .grossAmount(gross)
                .paymentIds(ids)
                .status(SettlementStatus.READY)
                .build();
    }

    public void calculateFee(BigDecimal feeRate) {
        this.feeAmount = this.grossAmount.multiply(feeRate).setScale(0, java.math.RoundingMode.HALF_UP);
        this.netAmount = this.grossAmount.subtract(this.feeAmount);
    }

    public void updateStatus(SettlementStatus status) {
        this.status = status;
    }
}
