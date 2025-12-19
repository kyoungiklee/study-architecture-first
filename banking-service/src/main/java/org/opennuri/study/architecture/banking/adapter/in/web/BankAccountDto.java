package org.opennuri.study.architecture.banking.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.BankAccount;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "은행 계좌 정보 DTO")
public class BankAccountDto {
    @Schema(description = "계좌 ID (생성 시 자동 부여)", example = "1")
    private Long id;

    @Schema(description = "회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long memberId;

    @Schema(description = "은행 코드", example = "001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bankCode;

    @Schema(description = "계좌 번호", example = "123-456-789", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String bankAccountNo;

    @Schema(description = "계좌 유효 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean valid;

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
