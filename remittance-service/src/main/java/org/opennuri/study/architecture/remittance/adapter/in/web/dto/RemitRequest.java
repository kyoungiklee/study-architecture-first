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

    @Schema(description = "송금 유형 (MEMBER: 내부 회원, BANK: 외부 은행)", example = "MEMBER", requiredMode = Schema.RequiredMode.REQUIRED)
    private RemittanceType toType;

    @Schema(description = "수취인 멤버십 ID (MEMBER 유형일 경우 필수)", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String toMembershipId;

    @Schema(description = "수취 은행 코드 (BANK 유형일 경우 필수)", example = "001", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String toBankCode;

    @Schema(description = "수취 은행 계좌 번호 (BANK 유형일 경우 필수)", example = "123-456-789", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String toBankAccountNumber;

    @Schema(description = "송금 금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @Schema(description = "멱등성 키", example = "uuid-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String idempotencyKey;
}
