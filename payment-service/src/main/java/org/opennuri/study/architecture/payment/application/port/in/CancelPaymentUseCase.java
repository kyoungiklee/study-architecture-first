package org.opennuri.study.architecture.payment.application.port.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

public interface CancelPaymentUseCase {
    void cancelPayment(CancelPaymentCommand command);

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class CancelPaymentCommand extends SelfValidating<CancelPaymentCommand> {
        private final Long paymentId;
        private final String reason;

        public CancelPaymentCommand(Long paymentId, String reason) {
            this.paymentId = paymentId;
            this.reason = reason;
            this.validateSelf();
        }
    }
}
