package org.opennuri.study.architecture.payment.application.port.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

/**
 * 결제 승인 유스케이스 인터페이스
 */
public interface ApprovePaymentUseCase {
    /**
     * 결제 승인 요청을 처리합니다.
     *
     * @param command 결제 승인 명령 (결제 상세 정보 및 멱등성 키 포함)
     */
    void approvePayment(ApprovePaymentCommand command);

    /**
     * 결제 승인 명령 객체
     * 입력값에 대한 유효성 검증을 포함합니다.
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class ApprovePaymentCommand extends SelfValidating<ApprovePaymentCommand> {
        /**
         * 가맹점 주문 ID
         */
        private final String orderId;
        /**
         * 결제 요청 사용자 ID
         */
        private final Long userId;
        /**
         * 결제 요청 금액
         */
        private final Long amount;
        /**
         * 결제 수단 (예: CARD)
         */
        private final String payMethod;
        /**
         * 참조 ID (외부 연동용)
         */
        private final String referenceId;
        /**
         * 중복 결제 방지를 위한 멱등성 키
         */
        private final String idempotencyKey;

        /**
         * ApprovePaymentCommand 생성자.
         * 모든 필드에 대한 null 체크 및 유효성 검증을 수행합니다.
         *
         * @param orderId 주문 ID
         * @param userId 사용자 ID
         * @param amount 금액
         * @param payMethod 결제 수단
         * @param referenceId 참조 ID
         * @param idempotencyKey 멱등성 키
         */
        public ApprovePaymentCommand(String orderId, Long userId, Long amount, String payMethod, String referenceId, String idempotencyKey) {
            this.orderId = orderId;
            this.userId = userId;
            this.amount = amount;
            this.payMethod = payMethod;
            this.referenceId = referenceId;
            this.idempotencyKey = idempotencyKey;
            this.validateSelf();
        }
    }
}
