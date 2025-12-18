package org.opennuri.study.architecture.payment.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.payment.application.port.out.PaymentRepositoryPort;
import org.opennuri.study.architecture.payment.domain.Payment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment savePayment(Payment payment) {
        PaymentJpaEntity entity = new PaymentJpaEntity(
                payment.getPaymentId(),
                payment.getMemberId(),
                payment.getAmount(),
                payment.getMerchantId(),
                payment.getStatus().name(),
                payment.getCreatedAt()
        );
        PaymentJpaEntity savedEntity = paymentJpaRepository.save(entity);
        return Payment.withId(
                savedEntity.getPaymentId(),
                savedEntity.getMemberId(),
                savedEntity.getAmount(),
                savedEntity.getMerchantId(),
                Payment.PaymentStatus.valueOf(savedEntity.getStatus()),
                savedEntity.getCreatedAt()
        );
    }
}
