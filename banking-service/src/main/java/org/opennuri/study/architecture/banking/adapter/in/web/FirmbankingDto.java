package org.opennuri.study.architecture.banking.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.opennuri.study.architecture.banking.domain.Firmbanking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirmbankingDto {

    private Long id;

    @NotNull(message = "회원 ID는 필수입니다")
    @Positive(message = "회원 ID는 양수여야 합니다")
    private Long memberId;

    @NotBlank(message = "출금 은행 코드는 필수입니다")
    private String fromBankCode;

    @NotBlank(message = "출금 계좌번호는 필수입니다")
    private String fromBankAccount;

    @NotBlank(message = "입금 은행 코드는 필수입니다")
    private String toBankCode;

    @NotBlank(message = "입금 계좌번호는 필수입니다")
    private String toBankAccount;

    @NotNull(message = "금액은 필수입니다")
    @Positive(message = "금액은 양수여야 합니다")
    private BigDecimal amount;

    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public static FirmbankingDto from(Firmbanking firmbanking) {
        if (firmbanking == null) {
            return null;
        }

        return FirmbankingDto.builder()
            .id(firmbanking.getId())
            .memberId(firmbanking.getMemberId())
            .fromBankCode(firmbanking.getFromBankCode())
            .fromBankAccount(firmbanking.getFromBankAccount())
            .toBankCode(firmbanking.getToBankCode())
            .toBankAccount(firmbanking.getToBankAccount())
            .amount(firmbanking.getAmount())
            .status(firmbanking.getStatus().name())
            .requestedAt(firmbanking.getRequestedAt())
            .completedAt(firmbanking.getCompletedAt())
            .build();
    }
}
