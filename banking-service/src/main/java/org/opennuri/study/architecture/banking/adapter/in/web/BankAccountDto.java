package org.opennuri.study.architecture.banking.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.BankAccount;

/**
 * 은행 계좌 정보를 전달하기 위한 데이터 전송 객체(DTO)입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "은행 계좌 정보 DTO")
public class BankAccountDto {
    /**
     * 계좌 고유 ID
     */
    @Schema(description = "계좌 ID (생성 시 자동 부여)", example = "1")
    private Long id;

    /**
     * 계좌 소유자 회원 ID
     */
    @Schema(description = "회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long memberId;

    /**
     * 은행 코드
     */
    @Schema(description = "은행 코드", example = "001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bankCode;

    /**
     * 계좌 번호
     */
    @Schema(description = "계좌 번호", example = "123-456-789", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bankAccountNo;

    /**
     * 계좌 유효 여부
     */
    @Schema(description = "계좌 유효 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean valid;

    /**
     * 도메인 모델을 DTO로 변환합니다.
     *
     * @param domain 계좌 도메인 모델
     * @return 변환된 DTO 객체
     */
    public static BankAccountDto from(BankAccount domain) {
        return BankAccountDto.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .bankCode(domain.getBankCode())
                .bankAccountNo(domain.getBankAccountNo())
                .valid(domain.isValid())
                .build();
    }
}
