package org.opennuri.study.architecture.query.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "상태 확인 API")
@WebAdapter
@RestController
public class QueryPingController {

    @Operation(summary = "Ping", description = "서비스 활성화 상태를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "성공 (pong 반환)")
    @GetMapping("/query/ping")
    public String ping() {
        return "pong";
    }
}
