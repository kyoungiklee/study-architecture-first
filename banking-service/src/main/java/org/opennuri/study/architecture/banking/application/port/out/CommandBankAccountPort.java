package org.opennuri.study.architecture.banking.application.port.out;

import org.opennuri.study.architecture.banking.domain.BankAccount;

public interface CommandBankAccountPort {
    BankAccount create(BankAccount account);
    BankAccount update(BankAccount account);
    boolean deleteById(Long id);
}
