package org.opennuri.study.architecture.remittance.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.remittance.domain.model.RemittanceType;

/**
 * 송금 요청 DTO
 */
@Schema(description = "송금 요청")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemitRequest {

    @Schema(description = "송금인 멤버십 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fromMembershipId;

    @Schema(description = "송금 유형 (MEMBERSHIP: 내부 회원, BANK_ACCOUNT: 외부 은행)", example = "MEMBERSHIP", requiredMode = Schema.RequiredMode.REQUIRED)
    private RemittanceType toType;

    @Schema(description = "수취 대상 (toMembershipId or toBankAccountNumber)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private String toTarget;

    @Schema(description = "송금 금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Schema(description = "송금 사유", example = "축의금")
    private String reason;
}
