package org.opennuri.study.architecture.money.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 머니 변동(충전/차감) 요청 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "머니 변동(충전/차감) 요청 데이터")
public class MoneyChangingRequest {

    @Schema(description = "대상 멤버십 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetMembershipId;

    @Schema(description = "변동 금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;
}
