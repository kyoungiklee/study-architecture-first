package org.opennuri.study.architecture.banking.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateBankAccountCommand {
    @NotNull Long memberId;
    @NotBlank String bankCode;
    @NotBlank String bankAccountNo;
    @NotNull Boolean valid;
}
