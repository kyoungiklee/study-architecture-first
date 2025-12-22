package org.opennuri.study.architecture.remittance.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 송금 결과 DTO
 */
@Schema(description = "송금 응답")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemitResponse {

    @Schema(description = "송금 ID", example = "100")
    private String remittanceId;

    @Schema(description = "송금 상태 (success, fail)", example = "success")
    private String status;
}
