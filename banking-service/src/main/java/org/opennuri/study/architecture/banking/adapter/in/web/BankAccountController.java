package org.opennuri.study.architecture.banking.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.in.command.UpdateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.service.BankAccountCommandService;
import org.opennuri.study.architecture.banking.application.service.BankAccountQueryService;
import org.opennuri.study.architecture.banking.domain.BankAccount;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/bank-accounts", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Bank Account", description = "뱅킹 계좌 관리 API")
public class BankAccountController {

    private final BankAccountCommandService commandService;
    private final BankAccountQueryService queryService;

    // ---- Commands ----
    @Operation(summary = "계좌 등록", description = "새로운 은행 계좌를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "계좌 등록 성공",
                    headers = @Header(name = "Location", description = "생성된 계좌의 URI", schema = @Schema(type = "string")),
                    content = @Content(schema = @Schema(implementation = BankAccountDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BankAccountDto> create(@Valid @RequestBody BankAccountDto request) {
        BankAccount saved = commandService.registerBankAccount(CreateBankAccountCommand.builder()
                .memberId(request.getMemberId())
                .bankCode(request.getBankCode())
                .bankAccountNo(request.getBankAccountNo())
                .valid(request.getValid())
                .build());

        BankAccountDto body = BankAccountDto.from(saved);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/bank-accounts/" + saved.getId()));
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }

    @Operation(summary = "계좌 수정", description = "ID에 해당하는 계좌 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계좌 수정 성공",
                    content = @Content(schema = @Schema(implementation = BankAccountDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
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

    @Operation(summary = "계좌 삭제", description = "ID에 해당하는 계좌를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "계좌 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = commandService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ---- Queries ----
    @Operation(summary = "계좌 단건 조회", description = "ID로 계좌 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BankAccountDto.class))),
            @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDto> getById(@PathVariable Long id) {
        return queryService.getById(id)
                .map(BankAccountDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "계좌 검색", description = "조건에 맞는 계좌 목록을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공",
            content = @Content(schema = @Schema(implementation = BankAccountDto.class)))
    @GetMapping
    public ResponseEntity<List<BankAccountDto>> search(
            @Parameter(description = "회원 ID") @RequestParam(required = false) Long memberId,
            @Parameter(description = "은행 코드") @RequestParam(required = false) String bankCode,
            @Parameter(description = "계좌 번호") @RequestParam(required = false) String bankAccountNo,
            @Parameter(description = "유효 여부") @RequestParam(required = false) Boolean valid,
            @Parameter(description = "생성일 시작 (ISO DATE_TIME)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdFrom,
            @Parameter(description = "생성일 종료 (ISO DATE_TIME)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdTo) {
        List<BankAccountDto> results = queryService.search(memberId, bankCode, bankAccountNo, valid, createdFrom, createdTo)
                .stream().map(BankAccountDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "계좌 이력 조회", description = "계좌 ID에 해당하는 변경 이력을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = BankAccountHistoryDto.class)))
    @GetMapping("/{id}/histories")
    public ResponseEntity<List<BankAccountHistoryDto>> getHistories(
            @Parameter(description = "계좌 ID") @PathVariable Long id,
            @Parameter(description = "작업 구분") @RequestParam(required = false) String action,
            @Parameter(description = "시작일") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @Parameter(description = "종료일") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {
        List<BankAccountHistoryDto> list = queryService.searchHistories(id, action, from, to)
                .stream().map(BankAccountHistoryDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "계좌 이력 단건 조회", description = "이력 ID로 특정 변경 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BankAccountHistoryDto.class))),
            @ApiResponse(responseCode = "404", description = "이력을 찾을 수 없음")
    })
    @GetMapping("/histories/{historyId}")
    public ResponseEntity<BankAccountHistoryDto> getHistoryById(
            @Parameter(description = "이력 ID") @PathVariable Long historyId) {
        return queryService.getHistoryById(historyId)
                .map(BankAccountHistoryDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
