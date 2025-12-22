package org.opennuri.study.architecture.payment.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제 서비스 상태 확인을 위한 컨트롤러
 */
@RestController
@RequestMapping("/payment")
@Tag(name = "Payment Ping", description = "결제 서비스 헬스 체크 API")
public class PaymentPingController {

    /**
     * 서비스 활성 상태 확인 엔드포인트
     *
     * @return 서비스가 정상 작동 중이면 "pong" 반환
     */
    @Operation(summary = "Ping (상태 확인)", description = "결제 서비스가 정상적으로 실행 중인지 확인합니다.")
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
