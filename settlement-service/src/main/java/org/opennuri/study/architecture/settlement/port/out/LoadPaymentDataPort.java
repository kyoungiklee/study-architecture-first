package org.opennuri.study.architecture.settlement.port.out;

import org.opennuri.study.architecture.settlement.domain.PaymentData;

import java.util.List;

public interface LoadPaymentDataPort {
    List<PaymentData> loadCompletedPayments();
}
