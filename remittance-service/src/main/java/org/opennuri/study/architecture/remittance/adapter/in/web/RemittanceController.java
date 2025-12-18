package org.opennuri.study.architecture.remittance.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitRequest;
import org.opennuri.study.architecture.remittance.adapter.in.web.dto.RemitResponse;
import org.opennuri.study.architecture.remittance.application.port.in.RequestRemittanceUseCase;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class RemittanceController {

    private final RequestRemittanceUseCase requestRemittanceUseCase;

    @PostMapping("/remittance")
    public RemitResponse requestRemittance(@RequestBody RemitRequest request) {
        RequestRemittanceUseCase.RemittanceCommand command = RequestRemittanceUseCase.RemittanceCommand.builder()
                .fromMembershipId(request.getFromMembershipId())
                .toType(request.getToType())
                .toMembershipId(request.getToMembershipId())
                .toBankCode(request.getToBankCode())
                .toBankAccountNumber(request.getToBankAccountNumber())
                .amount(request.getAmount())
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        return requestRemittanceUseCase.requestRemittance(command);
    }
}
