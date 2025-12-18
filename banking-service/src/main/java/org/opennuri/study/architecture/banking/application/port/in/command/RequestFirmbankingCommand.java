package org.opennuri.study.architecture.banking.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opennuri.study.archtecture.common.SelfValidating;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class RequestFirmbankingCommand extends SelfValidating<RequestFirmbankingCommand> {

    @NotNull
    @Positive
    private Long memberId;

    @NotBlank
    private String fromBankCode;

    @NotBlank
    private String fromBankAccount;

    @NotBlank
    private String toBankCode;

    @NotBlank
    private String toBankAccount;

    @NotNull
    @Positive
    private BigDecimal amount;

    public RequestFirmbankingCommand(Long memberId, String fromBankCode, String fromBankAccount,
                                     String toBankCode, String toBankAccount, BigDecimal amount) {
        this.memberId = memberId;
        this.fromBankCode = fromBankCode;
        this.fromBankAccount = fromBankAccount;
        this.toBankCode = toBankCode;
        this.toBankAccount = toBankAccount;
        this.amount = amount;
        this.validateSelf();
    }
}
