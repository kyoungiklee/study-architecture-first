package org.opennuri.study.architecture.remittance.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Remittance {
    String remittanceId;
    String fromMembershipId;
    RemittanceType toType;
    String toMembershipId;
    String toBankCode;
    String toBankAccountNumber;
    Long amount;
    RemittanceStatus status;
    String idempotencyKey;

    public static Remittance createRemittance(
            String fromMembershipId,
            RemittanceType toType,
            String toMembershipId,
            String toBankCode,
            String toBankAccountNumber,
            Long amount,
            String idempotencyKey
    ) {
        return new Remittance(
                null,
                fromMembershipId,
                toType,
                toMembershipId,
                toBankCode,
                toBankAccountNumber,
                amount,
                RemittanceStatus.REQUESTED,
                idempotencyKey
        );
    }
}
