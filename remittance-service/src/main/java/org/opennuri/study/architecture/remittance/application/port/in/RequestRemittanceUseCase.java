package org.opennuri.study.architecture.remittance.application.port.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.archtecture.common.SelfValidating;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface RequestRemittanceUseCase {
    RemitResponse requestRemittance(RemittanceCommand command);

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class RemittanceCommand extends SelfValidating<RemittanceCommand> {
        @NotBlank
        private String fromMembershipId;
        @NotNull
        private RemittanceType toType;
        
        private String toMembershipId;
        
        private String toBankCode;
        private String toBankAccountNumber;
        
        @NotNull
        @Positive
        private Long amount;
        
        @NotBlank
        private String idempotencyKey;

        public RemittanceCommand(String fromMembershipId, RemittanceType toType, String toMembershipId, String toBankCode, String toBankAccountNumber, Long amount, String idempotencyKey) {
            this.fromMembershipId = fromMembershipId;
            this.toType = toType;
            this.toMembershipId = toMembershipId;
            this.toBankCode = toBankCode;
            this.toBankAccountNumber = toBankAccountNumber;
            this.amount = amount;
            this.idempotencyKey = idempotencyKey;
            this.validateSelf();
        }
    }
}
