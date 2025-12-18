package org.opennuri.study.architecture.settlement.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.settlement.port.in.RunSettlementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settlement/jobs")
@RequiredArgsConstructor
public class JobsController {

    private final RunSettlementUseCase runSettlementUseCase;

    @PostMapping("/run-once")
    public ResponseEntity<String> runOnce(@RequestParam(name = "runId", required = false) String runId) {
        runSettlementUseCase.runOnce(runId);
        return ResponseEntity.accepted().body("RUN_REQUESTED");
    }
}
