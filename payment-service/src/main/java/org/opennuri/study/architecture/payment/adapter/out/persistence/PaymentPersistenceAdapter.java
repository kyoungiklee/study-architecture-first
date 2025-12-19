package org.opennuri.study.architecture.payment.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.payment.application.port.out.PaymentRepositoryPort;
import org.opennuri.study.architecture.payment.domain.Payment;
import org.springframework.stereotype.Component;

/**
 * 결제 영속성 처리를 위한 어댑터
 * 결제 정보를 데이터베이스에 저장하고 조회하는 기능을 담당합니다.
 */
@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;

    /**
     * 결제 도메인 엔티티를 JPA 엔티티로 변환하여 저장합니다.
     *
     * @param payment 저장할 결제 도메인 엔티티
     * @return 저장된 결제 도메인 엔티티
     */
    @Override
    public Payment savePayment(Payment payment) {
        PaymentJpaEntity entity = new PaymentJpaEntity(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPayMethod(),
                payment.getReferenceId(),
                payment.getStatus().name(),
                payment.getIdempotencyKey(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
        PaymentJpaEntity savedEntity = paymentJpaRepository.save(entity);
        return mapToDomainEntity(savedEntity);
    }

    /**
     * ID를 기반으로 결제 정보를 조회합니다.
     *
     * @param paymentId 결제 고유 ID
     * @return 조회된 결제 도메인 엔티티 (없을 경우 null)
     */
    @Override
    public Payment findPaymentById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId)
                .map(this::mapToDomainEntity)
                .orElse(null);
    }

    /**
     * 멱등성 키를 기반으로 결제 정보를 조회합니다.
     *
     * @param idempotencyKey 멱등성 키
     * @return 조회된 결제 도메인 엔티티 (없을 경우 null)
     */
    @Override
    public Payment findPaymentByIdempotencyKey(String idempotencyKey) {
        return paymentJpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(this::mapToDomainEntity)
                .orElse(null);
    }

    /**
     * JPA 엔티티를 도메인 엔티티로 매핑합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 엔티티
     */
    private Payment mapToDomainEntity(PaymentJpaEntity entity) {
        return Payment.withId(
                entity.getPaymentId(),
                entity.getOrderId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getPayMethod(),
                entity.getReferenceId(),
                Payment.PaymentStatus.valueOf(entity.getStatus()),
                entity.getIdempotencyKey(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
