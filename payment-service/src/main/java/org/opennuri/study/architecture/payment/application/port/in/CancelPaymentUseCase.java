package org.opennuri.study.architecture.payment.application.port.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

/**
 * 결제 취소 유스케이스 인터페이스
 */
public interface CancelPaymentUseCase {
    /**
     * 결제 취소 요청을 처리합니다.
     *
     * @param command 결제 취소 명령 (결제 ID 및 취소 사유 포함)
     */
    void cancelPayment(CancelPaymentCommand command);

    /**
     * 결제 취소 명령 객체
     * 입력값에 대한 유효성 검증을 포함합니다.
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class CancelPaymentCommand extends SelfValidating<CancelPaymentCommand> {
        /**
         * 취소할 결제의 고유 ID
         */
        private final Long paymentId;
        /**
         * 결제 취소 사유
         */
        private final String reason;

        /**
         * CancelPaymentCommand 생성자.
         * 모든 필드에 대한 null 체크 및 유효성 검증을 수행합니다.
         *
         * @param paymentId 결제 ID
         * @param reason 취소 사유
         */
        public CancelPaymentCommand(Long paymentId, String reason) {
            this.paymentId = paymentId;
            this.reason = reason;
            this.validateSelf();
        }
    }
}
