package org.opennuri.study.architecture.settlement.application.service;

import java.math.BigDecimal;

public interface FeeCalculator {
    BigDecimal calculateFeeRate(String merchantId);
}
