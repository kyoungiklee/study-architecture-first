package org.opennuri.study.architecture.banking.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.RegisterBankAccountUseCase;
import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.in.command.UpdateBankAccountCommand;
import org.opennuri.study.architecture.banking.application.port.out.CommandBankAccountPort;
import org.opennuri.study.architecture.banking.domain.BankAccount;
import org.opennuri.study.archtecture.common.UseCase;

@UseCase
@RequiredArgsConstructor
public class BankAccountCommandService implements RegisterBankAccountUseCase {

    private final CommandBankAccountPort commandBankAccountPort;

    @Override
    public BankAccount registerBankAccount(CreateBankAccountCommand command) {
        BankAccount account = BankAccount.builder()
                .memberId(command.getMemberId())
                .bankCode(command.getBankCode())
                .bankAccountNo(command.getBankAccountNo())
                .valid(command.getValid())
                .build();
        return commandBankAccountPort.create(account);
    }

    public BankAccount update(UpdateBankAccountCommand command) {
        BankAccount account = BankAccount.builder()
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
