package org.opennuri.study.architecture.payment.application.port.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

public interface ApprovePaymentUseCase {
    void approvePayment(ApprovePaymentCommand command);

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class ApprovePaymentCommand extends SelfValidating<ApprovePaymentCommand> {
        private final Long memberId;
        private final Long amount;
        private final String merchantId;
        private final String idempotencyKey;

        public ApprovePaymentCommand(Long memberId, Long amount, String merchantId, String idempotencyKey) {
            this.memberId = memberId;
            this.amount = amount;
            this.merchantId = merchantId;
            this.idempotencyKey = idempotencyKey;
            this.validateSelf();
        }
    }
}
