package org.opennuri.study.architecture.settlement.application.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DefaultFeeCalculator implements FeeCalculator {
    @Override
    public BigDecimal calculateFeeRate(String merchantId) {
        // 기본 수수료율 3% (예시)
        return new BigDecimal("0.03");
    }
}
