package org.opennuri.study.architecture.payment.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.payment.domain.Payment;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "결제 정보 응답 데이터")
public class PaymentResponse {

    @Schema(description = "결제 ID (내부 식별자)", example = "1")
    private Long paymentId;

    @Schema(description = "주문 ID (가맹점 주문 번호)", example = "ORD-20231027-001")
    private String orderId;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "결제 금액", example = "10000")
    private Long amount;

    @Schema(description = "결제 수단", example = "CARD", allowableValues = {"CARD", "TRANSFER", "CASH"})
    private String payMethod;

    @Schema(description = "결제 상태", example = "APPROVED", 
            allowableValues = {"CREATED", "REQUESTED", "APPROVED", "SETTLEMENT_PENDING", "SETTLED", "FAILED", "CANCELED"})
    private String status;

    @Schema(description = "결제 생성 시각", example = "2023-10-27T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "결제 수정 시각", example = "2023-10-27T10:05:00")
    private LocalDateTime updatedAt;

    /**
     * 결제 도메인 엔티티로부터 응답 객체를 생성합니다.
     *
     * @param payment 결제 도메인 엔티티
     * @return 결제 응답 DTO
     */
    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .payMethod(payment.getPayMethod())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
