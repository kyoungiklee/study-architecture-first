package org.opennuri.study.architecture.remittance.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Remittance {
    private final String remittanceId;
    private final String fromMembershipId;
    private final RemittanceType toType;
    private final String toTarget; // toMembershipId or toBankAccountNumber
    private final Long amount;
    private final String reason;
    private RemittanceStatus status;
    private String failureCode;
    private final Long createdAt;
    private Long updatedAt;

    public static Remittance createRemittance(
            String remittanceId,
            String fromMembershipId,
            RemittanceType toType,
            String toTarget,
            Long amount,
            String reason,
            RemittanceStatus status,
            Long createdAt
    ) {
        return new Remittance(
                remittanceId,
                fromMembershipId,
                toType,
                toTarget,
                amount,
                reason,
                status,
                null,
                createdAt,
                createdAt
        );
    }

    public void updateStatus(RemittanceStatus status, String failureCode) {
        this.status = status;
        this.failureCode = failureCode;
        this.updatedAt = System.currentTimeMillis();
    }
}
