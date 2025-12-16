package org.opennuri.study.architecture.banking.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.query.GetBankAccountQuery;
import org.opennuri.study.architecture.banking.application.port.in.query.SearchBankAccountQuery;
import org.opennuri.study.architecture.banking.application.port.out.QueryBankAccountHistoryPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryBankAccountPort;
import org.opennuri.study.architecture.banking.domain.RegisteredBankAccount;
import org.opennuri.study.architecture.banking.domain.RegisteredBankAccountHistory;
import org.opennuri.study.archtecture.common.UseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@UseCase
@RequiredArgsConstructor
public class BankAccountQueryService implements GetBankAccountQuery, SearchBankAccountQuery {

    private final QueryBankAccountPort queryBankAccountPort;
    private final QueryBankAccountHistoryPort queryBankAccountHistoryPort;

    @Override
    public Optional<RegisteredBankAccount> getById(Long id) {
        return queryBankAccountPort.findById(id);
    }

    @Override
    public List<RegisteredBankAccount> search(Long memberId, String bankCode, String bankAccountNo, Boolean valid, LocalDateTime createdFrom, LocalDateTime createdTo) {
        return queryBankAccountPort.search(memberId, bankCode, bankAccountNo, valid, createdFrom, createdTo);
    }

    public Optional<RegisteredBankAccountHistory> getHistoryById(Long id) {
        return queryBankAccountHistoryPort.findHistoryById(id);
    }

    public List<RegisteredBankAccountHistory> searchHistories(Long bankAccountId, String action, LocalDateTime from, LocalDateTime to) {
        return queryBankAccountHistoryPort.search(bankAccountId, action, from, to);
    }
}
