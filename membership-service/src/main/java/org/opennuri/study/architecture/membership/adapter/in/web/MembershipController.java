package org.opennuri.study.architecture.membership.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.opennuri.study.archtecture.common.WebAdapter;
import org.opennuri.study.architecture.membership.application.port.in.*;
import org.opennuri.study.architecture.membership.domain.Membership;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 멤버십 관련 웹 요청을 처리하는 컨트롤러입니다.
 * 인바운드 포트(UseCase)를 호출하여 멤버십 비즈니스 로직을 실행합니다.
 */
@WebAdapter
@RestController
@RequestMapping("/memberships")
@RequiredArgsConstructor
@Tag(name = "Membership", description = "멤버십 관리 API")
public class MembershipController {

    private final RegisterMembershipUseCase registerMembershipUseCase;
    private final UpdateMembershipUseCase updateMembershipUseCase;
    private final GetMembershipByIdUseCase getMembershipByIdUseCase;
    private final SearchMembershipUseCase searchMembershipUseCase;
    private final DeleteMembershipUseCase deleteMembershipUseCase;

    /**
     * 서비스 상태 확인 엔드포인트
     *
     * @return "OK" 문자열
     */
    @Operation(
            summary = "서비스 상태 확인",
            description = "멤버십 서비스의 정상 작동 여부를 확인합니다.",
            hidden = true  // Swagger UI에서 숨김 (테스트용)
    )
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OK");
    }

    /**
     * 신규 멤버십을 등록합니다.
     *
     * @param request 멤버십 등록 요청 데이터
     * @return 등록된 멤버십 정보와 201 Created 응답
     */
    @Operation(
            summary = "멤버십 등록",
            description = "새로운 멤버십을 등록합니다. 이름, 이메일, 주소는 필수 입력 항목입니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "멤버십 등록 성공",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<MembershipResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "멤버십 등록 정보",
                    required = true
            )
            @Valid @RequestBody RegisterMembershipRequest request) {
        RegisterMembershipCommand command = RegisterMembershipCommand.builder()
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .isCorp(request.isCorp())
                .build();

        Membership created = registerMembershipUseCase.registerMembership(command);
        MembershipResponse body = MembershipResponse.from(created);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/memberships/" + created.getMembershipId()));
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }

    /**
     * ID를 기준으로 멤버십 상세 정보를 조회합니다.
     *
     * @param id 멤버십 ID
     * @return 멤버십 상세 정보
     */
    @Operation(
            summary = "멤버십 단건 조회",
            description = "ID로 특정 멤버십의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버십을 찾을 수 없음"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MembershipResponse> getById(
            @Parameter(description = "멤버십 ID", example = "M001", required = true)
            @PathVariable("id") String id) {
        Optional<Membership> optional = getMembershipByIdUseCase.getMembershipById(id);
        return optional
                .map(MembershipResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * 조건에 맞는 멤버십 목록을 검색합니다.
     *
     * @param name 이름 검색어 (부분 일치)
     * @param email 이메일 검색어 (부분 일치)
     * @param address 주소 검색어 (부분 일치)
     * @param isCorp 법인 여부 필터
     * @param isValid 유효 상태 필터
     * @return 검색된 멤버십 목록
     */
    @Operation(
            summary = "멤버십 목록 조회",
            description = "조건에 맞는 멤버십 목록을 조회합니다. 모든 파라미터는 선택사항이며, 파라미터 없이 요청 시 전체 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<MembershipResponse>> search(
            @Parameter(description = "이름 (부분 일치)", example = "홍길동")
            @RequestParam(value = "name", required = false) String name,

            @Parameter(description = "이메일 (부분 일치)", example = "hong@example.com")
            @RequestParam(value = "email", required = false) String email,

            @Parameter(description = "주소 (부분 일치)", example = "서울")
            @RequestParam(value = "address", required = false) String address,

            @Parameter(description = "법인 여부", example = "true")
            @RequestParam(value = "isCorp", required = false) Boolean isCorp,

            @Parameter(description = "유효 상태", example = "true")
            @RequestParam(value = "isValid", required = false) Boolean isValid
    ) {
        SearchMembershipQuery query = SearchMembershipQuery.builder()
                .name(name)
                .email(email)
                .address(address)
                .isCorp(isCorp)
                .isValid(isValid)
                .build();
        List<MembershipResponse> result = searchMembershipUseCase.searchMemberships(query)
                .stream()
                .map(MembershipResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * 기존 멤버십 정보를 수정합니다.
     *
     * @param id 멤버십 ID
     * @param request 수정할 데이터
     * @return 수정된 멤버십 정보
     */
    @Operation(
            summary = "멤버십 수정",
            description = "기존 멤버십의 정보를 수정합니다. 모든 필드를 포함하여 요청해야 합니다 (PUT 방식)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = MembershipResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버십을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<MembershipResponse> update(
            @Parameter(description = "멤버십 ID", example = "M001", required = true)
            @PathVariable("id") String id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 멤버십 정보",
                    required = true
            )
            @Valid @RequestBody UpdateMembershipRequest request) {
        UpdateMembershipCommand command = UpdateMembershipCommand.builder()
                .membershipId(id)
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .isCorp(request.isCorp())
                .build();
        Membership updated = updateMembershipUseCase.updateMembership(command);
        return ResponseEntity.ok(MembershipResponse.from(updated));
    }

    /**
     * ID를 기준으로 멤버십을 삭제합니다.
     *
     * @param id 멤버십 ID
     * @return 204 No Content (성공 시)
     */
    @Operation(
            summary = "멤버십 삭제",
            description = "ID에 해당하는 멤버십을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공 (응답 본문 없음)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버십을 찾을 수 없음"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "멤버십 ID", example = "M001", required = true)
            @PathVariable("id") String id) {
        boolean deleted = deleteMembershipUseCase.deleteMembership(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}