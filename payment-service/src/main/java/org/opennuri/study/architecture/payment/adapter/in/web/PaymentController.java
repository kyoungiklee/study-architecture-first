package org.opennuri.study.architecture.payment.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.payment.application.port.in.ApprovePaymentUseCase;
import org.opennuri.study.architecture.payment.application.port.in.CancelPaymentUseCase;
import org.opennuri.study.architecture.payment.application.port.in.GetPaymentUseCase;
import org.opennuri.study.architecture.payment.domain.Payment;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Payment API Controller
 * Provides endpoints for payment approval, cancellation, and retrieval.
 */
@WebAdapter
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 승인, 취소 및 조회를 위한 API")
public class PaymentController {

    private final ApprovePaymentUseCase approvePaymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;

    /**
     * 결제 승인 요청
     *
     * @param idempotencyKey 멱등성 키
     * @param request 결제 승인 요청 데이터
     * @return 결제 정보 응답
     */
    @Operation(summary = "결제 승인 요청", description = "새로운 결제 승인을 요청합니다. 멱등성 키를 필수적으로 전달해야 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 승인 요청 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/approve")
    public ResponseEntity<PaymentResponse> approvePayment(
            @Parameter(description = "멱등성 키 (중복 요청 방지용)", required = true, example = "uuid-1234-5678") 
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "결제 승인 요청 정보", required = true)
            @Valid @RequestBody PaymentRequest request) {

        ApprovePaymentUseCase.ApprovePaymentCommand command = ApprovePaymentUseCase.ApprovePaymentCommand.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .payMethod(request.getPayMethod())
                .referenceId(request.getReferenceId())
                .idempotencyKey(idempotencyKey)
                .build();

        approvePaymentUseCase.approvePayment(command);

        // 승인 요청 결과를 조회하여 반환 (멱등성 고려)
        // 실제로는 서비스에서 저장된 엔티티를 반환하는 것이 더 효율적일 수 있음
        return ResponseEntity.ok().build(); // TODO: 임시 반환, 실제 구현 시 저장된 데이터 반환 로직 추가 필요
    }

    /**
     * 결제 취소 요청
     *
     * @param paymentId 결제 ID
     * @param idempotencyKey 멱등성 키
     * @param request 취소 사유
     * @return 결제 정보 응답
     */
    @Operation(summary = "결제 취소 요청", description = "기존 결제를 취소합니다. 취소 사유를 함께 전달해야 하며, APPROVED 상태의 결제만 취소 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "취소 불가능한 상태 또는 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "내부 서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPayment(
            @Parameter(description = "취소할 결제 ID", required = true, example = "1") @PathVariable("paymentId") Long paymentId,
            @Parameter(description = "멱등성 키", required = true, example = "uuid-1234-5678") @RequestHeader("Idempotency-Key") String idempotencyKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "결제 취소 요청 정보", required = true)
            @RequestBody CancelRequest request) {

        CancelPaymentUseCase.CancelPaymentCommand command = CancelPaymentUseCase.CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .reason(request.getReason())
                .build();

        cancelPaymentUseCase.cancelPayment(command);
        return ResponseEntity.ok().build();
    }

    /**
     * 결제 단건 조회
     *
     * @param paymentId 결제 ID
     * @return 결제 정보 응답
     */
    @Operation(summary = "결제 단건 조회", description = "특정 결제의 상세 정보를 조회합니다. 결제 ID를 기준으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "조회할 결제 ID", required = true, example = "1") @PathVariable("paymentId") Long paymentId) {
        return getPaymentUseCase.getPayment(paymentId)
                .map(PaymentResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelRequest {
        @Schema(description = "취소 사유", example = "고객 변심")
        private String reason;
    }
}
