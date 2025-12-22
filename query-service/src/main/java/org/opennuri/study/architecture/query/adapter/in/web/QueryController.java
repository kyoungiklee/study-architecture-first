package org.opennuri.study.architecture.query.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.query.application.port.in.GetAggregatedSummaryUseCase;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Query", description = "조회 및 집계 API")
@WebAdapter
@RestController
@RequiredArgsConstructor
public class QueryController {

    private final GetAggregatedSummaryUseCase getAggregatedSummaryUseCase;

    @Operation(summary = "회원 요약 정보 조회", description = "회원 정보, 잔액, 거래 내역 등을 집계하여 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/query/member-summary/{memberId}")
    public Map<String, Object> getMemberSummary(
            @Parameter(description = "회원 ID", example = "M001")
            @PathVariable String memberId) {
        return getAggregatedSummaryUseCase.getAggregatedSummary(memberId);
    }
}
