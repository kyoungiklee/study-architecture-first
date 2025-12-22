package org.opennuri.study.architecture.payment.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "결제 승인 요청 데이터")
public class PaymentRequest {

    @NotBlank(message = "orderId is mandatory")
    @Schema(description = "주문 ID (가맹점 주문 번호)", example = "ORD-20231027-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @NotNull(message = "userId is mandatory")
    @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "amount is mandatory")
    @Positive(message = "amount must be positive")
    @Schema(description = "결제 금액", example = "10000", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @NotBlank(message = "payMethod is mandatory")
    @Schema(description = "결제 수단", example = "CARD", allowableValues = {"CARD", "TRANSFER", "CASH"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String payMethod;

    @Schema(description = "참조 ID (외부 시스템 연동용)", example = "REF-12345")
    private String referenceId;
}
