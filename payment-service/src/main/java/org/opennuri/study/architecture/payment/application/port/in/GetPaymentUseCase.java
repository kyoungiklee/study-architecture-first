package org.opennuri.study.architecture.payment.application.port.in;

import org.opennuri.study.architecture.payment.domain.Payment;

import java.util.Optional;

/**
 * 결제 정보 조회 유스케이스 인터페이스
 */
public interface GetPaymentUseCase {
    /**
     * 결제 ID를 기반으로 결제 상세 정보를 조회합니다.
     *
     * @param paymentId 조회할 결제의 고유 ID
     * @return 결제 정보 (존재하지 않을 경우 Optional.empty())
     */
    Optional<Payment> getPayment(Long paymentId);
}
