package org.opennuri.study.architecture.remittance.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemitRequest {
    private String fromMembershipId;
    private RemittanceType toType;
    private String toMembershipId;
    private String toBankCode;
    private String toBankAccountNumber;
    private Long amount;
    private String idempotencyKey;
}
