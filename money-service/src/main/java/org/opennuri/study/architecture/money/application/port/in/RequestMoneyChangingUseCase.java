package org.opennuri.study.architecture.money.application.port.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 머니 변동(충전/차감)을 요청하는 Use Case 인터페이스입니다.
 */
public interface RequestMoneyChangingUseCase {
    /**
     * 머니 충전을 수행합니다.
     *
     * @param request 머니 충전 요청 정보
     */
    void increaseMoney(IncreaseMoneyRequest request);

    /**
     * 머니 차감을 수행합니다.
     *
     * @param request 머니 차감 요청 정보
     */
    void decreaseMoney(DecreaseMoneyRequest request);

    /**
     * 머니 충전 요청 데이터 클래스
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class IncreaseMoneyRequest extends SelfValidating<IncreaseMoneyRequest> {
        @NotBlank
        private String targetMembershipId;
        @NotNull
        @Positive
        private Long amount;

        public IncreaseMoneyRequest(String targetMembershipId, Long amount) {
            this.targetMembershipId = targetMembershipId;
            this.amount = amount;
            this.validateSelf();
        }
    }

    /**
     * 머니 차감 요청 데이터 클래스
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    class DecreaseMoneyRequest extends SelfValidating<DecreaseMoneyRequest> {
        @NotBlank
        private String targetMembershipId;
        @NotNull
        @Positive
        private Long amount;

        public DecreaseMoneyRequest(String targetMembershipId, Long amount) {
            this.targetMembershipId = targetMembershipId;
            this.amount = amount;
            this.validateSelf();
        }
    }
}
