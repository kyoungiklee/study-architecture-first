package org.opennuri.study.architecture.payment.application.port.out;

import org.opennuri.study.architecture.payment.domain.Payment;

public interface PaymentRepositoryPort {
    Payment savePayment(Payment payment);
}
