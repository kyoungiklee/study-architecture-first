package org.opennuri.study.architecture.payment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 결제 정보를 위한 Spring Data JPA 리포지토리 인터페이스
 */
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    /**
     * 멱등성 키를 기반으로 결제 엔티티를 조회합니다.
     *
     * @param idempotencyKey 멱등성 키
     * @return 결제 엔티티 (Optional)
     */
    Optional<PaymentJpaEntity> findByIdempotencyKey(String idempotencyKey);
}
