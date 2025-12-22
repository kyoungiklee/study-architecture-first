package org.opennuri.study.architecture.remittance.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitRequest;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.architecture.remittance.application.port.in.RequestRemittanceUseCase;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.opennuri.study.architecture.remittance.application.port.out.RemittanceRepositoryPort;
import org.opennuri.study.architecture.remittance.domain.model.Remittance;
import org.springframework.web.bind.annotation.*;

/**
 * 송금 컨트롤러
 * 송금 요청을 처리하는 웹 어댑터입니다.
 */
@Tag(name = "Remittance", description = "송금 관련 API")
@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RemittanceController {

    private final RequestRemittanceUseCase requestRemittanceUseCase;
    private final RemittanceRepositoryPort remittanceRepositoryPort;

    /**
     * 송금을 요청합니다.
     * 송금 유형(회원간, 은행간)에 따라 적절한 송금 처리를 수행합니다.
     *
     * @param request 송금 요청 정보
     * @return 송금 처리 결과
     */
    @Operation(summary = "송금 요청", description = "송금 요청을 처리합니다.")
    @ApiResponse(responseCode = "200", description = "송금 요청 성공")
    @PostMapping("/remittances")
    public RemitResponse requestRemittance(@RequestBody RemitRequest request) {
        RequestRemittanceUseCase.RemittanceCommand command = RequestRemittanceUseCase.RemittanceCommand.builder()
                .fromMembershipId(request.getFromMembershipId())
                .toType(request.getToType())
                .toTarget(request.getToTarget())
                .amount(request.getAmount())
                .reason(request.getReason())
                .build();

        return requestRemittanceUseCase.requestRemittance(command);
    }

    @Operation(summary = "송금 상태 조회", description = "송금 상태를 조회합니다.")
    @GetMapping("/remittances/{remittanceId}")
    public Remittance getRemittance(@PathVariable String remittanceId) {
        return remittanceRepositoryPort.findByRemittanceId(remittanceId);
    }
}
