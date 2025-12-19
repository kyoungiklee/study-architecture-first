package org.opennuri.study.architecture.money.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.money.application.port.in.GetMemberMoneyUseCase;
import org.opennuri.study.architecture.money.application.port.in.GetMoneyChangingHistoryUseCase;
import org.opennuri.study.architecture.money.domain.MoneyChangingHistory;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 머니 서비스의 조회(잔액, 이력) 요청을 처리하는 웹 어댑터 컨트롤러입니다.
 */
@WebAdapter
@RestController
@RequiredArgsConstructor
@Tag(name = "Money Query", description = "머니 조회(잔액/이력) API")
public class MoneyQueryController {

    private final GetMemberMoneyUseCase getMemberMoneyUseCase;
    private final GetMoneyChangingHistoryUseCase getMoneyChangingHistoryUseCase;

    /**
     * 특정 멤버십의 머니 잔액을 조회합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 잔액
     */
    @Operation(summary = "머니 잔액 조회", description = "대상 멤버십의 현재 머니 잔액을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", 
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "404", description = "멤버십을 찾을 수 없음")
    })
    @GetMapping(path = "/money/{membershipId}")
    Long getMemberMoney(
            @Parameter(description = "멤버십 ID", example = "1", required = true)
            @PathVariable String membershipId) {
        return getMemberMoneyUseCase.getMemberMoney(membershipId);
    }

    /**
     * 특정 멤버십의 머니 변동 이력을 조회합니다.
     *
     * @param membershipId 멤버십 ID
     * @return 머니 변동 이력 목록
     */
    @Operation(summary = "머니 변동 이력 조회", description = "대상 멤버십의 머니 변동 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MoneyChangingHistory.class)))),
            @ApiResponse(responseCode = "404", description = "멤버십을 찾을 수 없음")
    })
    @GetMapping(path = "/money/{membershipId}/history")
    List<MoneyChangingHistory> getHistory(
            @Parameter(description = "멤버십 ID", example = "1", required = true)
            @PathVariable String membershipId) {
        return getMoneyChangingHistoryUseCase.getHistory(membershipId);
    }
}
