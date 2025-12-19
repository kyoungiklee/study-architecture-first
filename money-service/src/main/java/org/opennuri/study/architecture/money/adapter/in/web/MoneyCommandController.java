package org.opennuri.study.architecture.money.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.money.application.port.in.RequestMoneyChangingUseCase;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 머니 서비스의 상태 변경(충전, 차감) 요청을 처리하는 웹 어댑터 컨트롤러입니다.
 */
@WebAdapter
@RestController
@RequiredArgsConstructor
@Tag(name = "Money Command", description = "머니 상태 변경(충전/차감) API")
public class MoneyCommandController {

    private final RequestMoneyChangingUseCase requestMoneyChangingUseCase;

    /**
     * 특정 멤버십의 머니를 충전합니다.
     *
     * @param request 머니 충전 요청 정보 (대상 멤버십 ID, 금액)
     */
    @Operation(summary = "머니 충전", description = "대상 멤버십의 머니 잔액을 증액합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "충전 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(path = "/money/increase")
    void increaseMoneyChangingRequest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "머니 충전 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MoneyChangingRequest.class))
            )
            @RequestBody MoneyChangingRequest request) {
        RequestMoneyChangingUseCase.IncreaseMoneyRequest command = RequestMoneyChangingUseCase.IncreaseMoneyRequest.builder()
                .targetMembershipId(request.getTargetMembershipId())
                .amount(request.getAmount())
                .build();
        requestMoneyChangingUseCase.increaseMoney(command);
    }

    /**
     * 특정 멤버십의 머니를 차감합니다.
     *
     * @param request 머니 차감 요청 정보 (대상 멤버십 ID, 금액)
     */
    @Operation(summary = "머니 차감", description = "대상 멤버십의 머니 잔액을 감액합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차감 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(path = "/money/decrease")
    void decreaseMoneyChangingRequest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "머니 차감 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MoneyChangingRequest.class))
            )
            @RequestBody MoneyChangingRequest request) {
        RequestMoneyChangingUseCase.DecreaseMoneyRequest command = RequestMoneyChangingUseCase.DecreaseMoneyRequest.builder()
                .targetMembershipId(request.getTargetMembershipId())
                .amount(request.getAmount())
                .build();
        requestMoneyChangingUseCase.decreaseMoney(command);
    }
}
