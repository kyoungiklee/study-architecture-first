package org.opennuri.study.architecture.banking.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    @Schema(description = "요청 경로", example = "/bank-accounts")
    private String path;

    @Schema(description = "필드별 에러 목록")
    private List<FieldError> errors;

    @Data
    @Builder
    @Schema(description = "필드 에러 상세")
    public static class FieldError {
        @Schema(description = "필드명", example = "bankCode")
        private String field;

        @Schema(description = "거부된 값", example = "INVALID_CODE")
        private Object rejectedValue;

        @Schema(description = "에러 메시지", example = "올바른 은행 코드가 아닙니다.")
        private String message;
    }
}
