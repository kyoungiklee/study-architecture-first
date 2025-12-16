package org.opennuri.study.architecture.banking.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.in.command.UpdateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.service.BankAccountCommandService;
import org.opennuri.study.architecture.banking.application.service.BankAccountQueryService;
import org.opennuri.study.architecture.banking.domain.BankAccount;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountCommandService commandService;
    private final BankAccountQueryService queryService;

    // ---- Commands ----
    @PostMapping
    public ResponseEntity<BankAccountDto> create(@Valid @RequestBody BankAccountDto request) {
        BankAccount saved = commandService.registerBankAccount(CreateBankAccountCommand.builder()
                .memberId(request.getMemberId())
                .bankCode(request.getBankCode())
                .bankAccountNo(request.getBankAccountNo())
                .valid(request.getValid())
                .build());
        return ResponseEntity.ok(BankAccountDto.from(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccountDto> update(@PathVariable Long id,
                                                 @Valid @RequestBody BankAccountDto request) {
        BankAccount updated = commandService.update(UpdateBankAccountCommand.builder()
                .id(id)
                .memberId(request.getMemberId())
                .bankCode(request.getBankCode())
                .bankAccountNo(request.getBankAccountNo())
                .valid(request.getValid())
                .build());
        return ResponseEntity.ok(BankAccountDto.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = commandService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ---- Queries ----
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDto> getById(@PathVariable Long id) {
        return queryService.getById(id)
                .map(BankAccountDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BankAccountDto>> search(@RequestParam(required = false) Long memberId,
                                                       @RequestParam(required = false) String bankCode,
                                                       @RequestParam(required = false) String bankAccountNo,
                                                       @RequestParam(required = false) Boolean valid,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       LocalDateTime createdFrom,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       LocalDateTime createdTo) {
        List<BankAccountDto> results = queryService.search(memberId, bankCode, bankAccountNo, valid, createdFrom, createdTo)
                .stream().map(BankAccountDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity<List<BankAccountHistoryDto>> getHistories(@PathVariable Long id,
                                                                    @RequestParam(required = false) String action,
                                                                    @RequestParam(required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                    LocalDateTime from,
                                                                    @RequestParam(required = false)
                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                    LocalDateTime to) {
        List<BankAccountHistoryDto> list = queryService.searchHistories(id, action, from, to)
                .stream().map(BankAccountHistoryDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/histories/{historyId}")
    public ResponseEntity<BankAccountHistoryDto> getHistoryById(@PathVariable Long historyId) {
        return queryService.getHistoryById(historyId)
                .map(BankAccountHistoryDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
