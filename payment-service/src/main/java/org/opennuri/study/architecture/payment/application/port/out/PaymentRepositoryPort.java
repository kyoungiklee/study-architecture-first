package org.opennuri.study.architecture.payment.application.port.out;

import org.opennuri.study.architecture.payment.domain.Payment;

/**
 * 결제 영속성 관리를 위한 출력 포트 인터페이스
 */
public interface PaymentRepositoryPort {
    /**
     * 결제 정보를 저장합니다.
     *
     * @param payment 저장할 결제 도메인 객체
     * @return 저장된 결제 도메인 객체
     */
    Payment savePayment(Payment payment);

    /**
     * 결제 ID로 결제 정보를 조회합니다.
     *
     * @param paymentId 조회할 결제 고유 ID
     * @return 조회된 결제 객체 (없을 경우 null)
     */
    Payment findPaymentById(Long paymentId);

    /**
     * 멱등성 키로 결제 정보를 조회합니다.
     * 중복 결제 요청 방지를 위해 사용됩니다.
     *
     * @param idempotencyKey 멱등성 키
     * @return 조회된 결제 객체 (없을 경우 null)
     */
    Payment findPaymentByIdempotencyKey(String idempotencyKey);
}
