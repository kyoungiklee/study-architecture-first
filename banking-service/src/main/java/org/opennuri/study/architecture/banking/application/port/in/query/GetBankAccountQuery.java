package org.opennuri.study.architecture.banking.application.port.in.query;

import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;

import java.util.Optional;

public interface GetBankAccountQuery {
    Optional<RegisteredBankAccount> getById(Long id);
}
