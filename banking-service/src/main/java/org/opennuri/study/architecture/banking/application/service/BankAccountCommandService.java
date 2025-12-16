package org.opennuri.study.architecture.banking.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.CreateBankAccountUseCase;
import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.in.command.UpdateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.out.CommandBankAccountPort;
import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;
import org.opennuri.study.archtecture.common.UseCase;

@UseCase
@RequiredArgsConstructor
public class BankAccountCommandService implements CreateBankAccountUseCase {

    private final CommandBankAccountPort commandBankAccountPort;

    @Override
    public RegisteredBankAccount create(CreateBankAccountCommand command) {
        RegisteredBankAccount account = RegisteredBankAccount.builder()
                .memberId(command.getMemberId())
                .bankCode(command.getBankCode())
                .bankAccountNo(command.getBankAccountNo())
                .valid(command.getValid())
                .build();
        return commandBankAccountPort.create(account);
    }

    public RegisteredBankAccount update(UpdateBankAccountCommand command) {
        RegisteredBankAccount account = RegisteredBankAccount.builder()
                .id(command.getId())
                .memberId(command.getMemberId())
                .bankCode(command.getBankCode())
                .bankAccountNo(command.getBankAccountNo())
                .valid(command.getValid())
                .build();
        return commandBankAccountPort.update(account);
    }

    public boolean delete(Long id) {
        return commandBankAccountPort.deleteById(id);
    }
}
