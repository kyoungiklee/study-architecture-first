package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;

public interface CommandBankAccountPort {
    RegisteredBankAccount create(RegisteredBankAccount account);
    RegisteredBankAccount update(RegisteredBankAccount account);
    boolean deleteById(Long id);
}
