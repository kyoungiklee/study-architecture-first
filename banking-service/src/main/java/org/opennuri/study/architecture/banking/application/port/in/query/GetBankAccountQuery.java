package org.opennuri.study.architecture.banking.application.port.in.query;

import org.opennuri.study.architecture.banking.domain.BankAccount;

import java.util.Optional;

public interface GetBankAccountQuery {
    Optional<BankAccount> getById(Long id);
}
