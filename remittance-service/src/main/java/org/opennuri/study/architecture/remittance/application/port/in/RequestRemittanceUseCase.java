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

/**
 * 송금 요청 유스케이스
 */
public interface RequestRemittanceUseCase {
    /**
     * 송금을 요청합니다.
     *
     * @param command 송금 명령
     * @return 송금 처리 결과
     */
    RemitResponse requestRemittance(RemittanceCommand command);

    /**
     * 송금 명령 DTO
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class RemittanceCommand extends SelfValidating<RemittanceCommand> {
        @NotBlank
        private String fromMembershipId;
        @NotNull
        private RemittanceType toType;
        
        @NotBlank
        private String toTarget;
        
        @NotNull
        @Positive
        private Long amount;

        private String reason;
        
        public RemittanceCommand(String fromMembershipId, RemittanceType toType, String toTarget, Long amount, String reason) {
            this.fromMembershipId = fromMembershipId;
            this.toType = toType;
            this.toTarget = toTarget;
            this.amount = amount;
            this.reason = reason;
            this.validateSelf();
        }
    }
}
