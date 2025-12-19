package org.opennuri.study.architecture.payment.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 에러 응답 DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 발생 시각", example = "2025-12-19T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;

    @Schema(description = "에러 타입", example = "Bad Request")
    private String error;

    @Schema(description = "에러 메시지", example = "유효성 검증에 실패했습니다.")
    private String message;

    @Schema(description = "요청 경로", example = "/api/v1/payments")
    private String path;

    @Schema(description = "필드별 에러 목록")
    private List<FieldError> errors;

    /**
     * 필드별 에러 상세 정보
     */
    @Data
    @Builder
    @Schema(description = "필드 에러 상세")
    public static class FieldError {
        @Schema(description = "필드명", example = "userId")
        private String field;

        @Schema(description = "거부된 값", example = "-1")
        private Object rejectedValue;

        @Schema(description = "에러 메시지", example = "사용자 ID는 양수여야 합니다.")
        private String message;
    }
}
