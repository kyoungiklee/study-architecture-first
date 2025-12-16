package org.opennuri.study.architecture.banking.application.port.in;

import org.opennuri.study.architecture.banking.application.port.in.command.CreateBankAccountCommand;
import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;

public interface CreateBankAccountUseCase {
    RegisteredBankAccount create(CreateBankAccountCommand command);
}
