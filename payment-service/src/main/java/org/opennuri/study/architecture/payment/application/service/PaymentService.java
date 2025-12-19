package org.opennuri.study.architecture.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.payment.application.port.in.ApprovePaymentUseCase;
import org.opennuri.study.architecture.payment.application.port.in.CancelPaymentUseCase;
import org.opennuri.study.architecture.payment.application.port.in.GetPaymentUseCase;
import org.opennuri.study.architecture.payment.application.port.out.PaymentRepositoryPort;
import org.opennuri.study.architecture.payment.domain.Payment;
import org.opennuri.study.archtecture.common.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 결제 서비스 구현체
 * 결제 승인, 취소 및 조회 비즈니스 로직을 담당합니다.
 */
@UseCase
@RequiredArgsConstructor
@Transactional
public class PaymentService implements ApprovePaymentUseCase, CancelPaymentUseCase, GetPaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;

    /**
     * 결제 승인 요청을 처리합니다.
     * 멱등성 키를 확인하여 중복 요청일 경우 기존 결과를 반환합니다.
     *
     * @param command 결제 승인 명령
     */
    @Override
    public void approvePayment(ApprovePaymentCommand command) {
        // 5.1 동일 Idempotency-Key면 기존 결과 반환
        Payment existingPayment = paymentRepositoryPort.findPaymentByIdempotencyKey(command.getIdempotencyKey());
        if (existingPayment != null) {
            return;
        }

        // 2.1 결제 승인 흐름 시작
        // Payment 상태 생성 (CREATED)
        Payment payment = Payment.withId(
                null,
                command.getOrderId(),
                command.getUserId(),
                command.getAmount(),
                command.getPayMethod(),
                command.getReferenceId(),
                Payment.PaymentStatus.CREATED,
                command.getIdempotencyKey(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        payment = paymentRepositoryPort.savePayment(payment);

        // TODO: Money에 잔액 예약 요청(RESERVE) - Saga 결합 필요
        // TODO: 예약 성공 시 APPROVED로 전이
        // TODO: Banking 이체 요청
        // TODO: Settlement 반영 요청
        // TODO: 최종 완료 상태 전이(SETTLED)
    }

    /**
     * 결제를 취소합니다.
     * APPROVED 상태의 결제만 취소가 가능합니다.
     *
     * @param command 결제 취소 명령
     */
    @Override
    public void cancelPayment(CancelPaymentCommand command) {
        Payment payment = paymentRepositoryPort.findPaymentById(command.getPaymentId());
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        // 2.2 취소 가능 여부 검사 (APPROVED 상태에서만 CANCELED로 전이 허용)
        if (payment.getStatus() != Payment.PaymentStatus.APPROVED) {
            throw new IllegalStateException("Payment cannot be cancelled in status: " + payment.getStatus());
        }

        // 상태 전이 CANCELED 기록
        Payment cancelledPayment = Payment.withId(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPayMethod(),
                payment.getReferenceId(),
                Payment.PaymentStatus.CANCELED,
                payment.getIdempotencyKey(),
                payment.getCreatedAt(),
                LocalDateTime.now()
        );
        paymentRepositoryPort.savePayment(cancelledPayment);
    }

    /**
     * 결제 상세 정보를 조회합니다.
     *
     * @param paymentId 결제 ID
     * @return 결제 정보 (Optional)
     */
    @Override
    public java.util.Optional<Payment> getPayment(Long paymentId) {
        return java.util.Optional.ofNullable(paymentRepositoryPort.findPaymentById(paymentId));
    }
}
