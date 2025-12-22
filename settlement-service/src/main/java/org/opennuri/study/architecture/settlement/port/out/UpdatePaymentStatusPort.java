package org.opennuri.study.architecture.settlement.port.out;

public interface UpdatePaymentStatusPort {
    void markSettled(String paymentId);
}
