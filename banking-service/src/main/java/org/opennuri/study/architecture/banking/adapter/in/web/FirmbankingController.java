package org.opennuri.study.architecture.banking.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.RequestFirmbankingUseCase;
import org.opennuri.study.architecture.banking.application.port.in.command.RequestFirmbankingCommand;
import org.opennuri.study.architecture.banking.application.port.out.QueryFirmbankingHistoryPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryFirmbankingPort;
import org.opennuri.study.architecture.banking.domain.Firmbanking;
import org.opennuri.study.architecture.banking.domain.FirmbankingHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/firmbanking")
@RequiredArgsConstructor
public class FirmbankingController {

    private final RequestFirmbankingUseCase requestFirmbankingUseCase;
    private final QueryFirmbankingPort queryFirmbankingPort;
    private final QueryFirmbankingHistoryPort queryFirmbankingHistoryPort;

    /**
     * 펌뱅킹 요청
     */
    @PostMapping
    public ResponseEntity<FirmbankingDto> requestFirmbanking(@Valid @RequestBody FirmbankingDto request) {
        RequestFirmbankingCommand command = RequestFirmbankingCommand.builder()
            .memberId(request.getMemberId())
            .fromBankCode(request.getFromBankCode())
            .fromBankAccount(request.getFromBankAccount())
            .toBankCode(request.getToBankCode())
            .toBankAccount(request.getToBankAccount())
            .amount(request.getAmount())
            .build();

        Firmbanking firmbanking = requestFirmbankingUseCase.requestFirmbanking(command);
        return ResponseEntity.ok(FirmbankingDto.from(firmbanking));
    }

    /**
     * 펌뱅킹 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<FirmbankingDto> getFirmbanking(@PathVariable Long id) {
        return queryFirmbankingPort.findById(id)
            .map(FirmbankingDto::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 회원별 펌뱅킹 목록 조회
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<FirmbankingDto>> getFirmbankingsByMember(@PathVariable Long memberId) {
        List<FirmbankingDto> results = queryFirmbankingPort.findByMemberId(memberId)
            .stream()
            .map(FirmbankingDto::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    /**
     * 펌뱅킹 이력 조회
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<FirmbankingHistory>> getFirmbankingHistory(@PathVariable Long id) {
        List<FirmbankingHistory> history = queryFirmbankingHistoryPort.findByFirmbankingId(id);
        return ResponseEntity.ok(history);
    }
}
