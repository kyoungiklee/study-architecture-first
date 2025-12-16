package org.opennuri.study.architecture.banking.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.out.CommandBankAccountPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryBankAccountHistoryPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryBankAccountPort;
import org.opennuri.study.architecture.banking.domain.BankAccount;
import org.opennuri.study.architecture.banking.domain.BankAccountHistory;
import org.opennuri.study.archtecture.common.PersistanceAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PersistanceAdapter
@RequiredArgsConstructor
public class BankingPersistenceAdapter implements CommandBankAccountPort,
        QueryBankAccountPort,
        QueryBankAccountHistoryPort {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountHistoryRepository bankAccountHistoryRepository;

    // ---- BankAccount (Command)
    @Override
    public BankAccount create(BankAccount account) {
        BankAccountEntity entity = BankAccountEntity.builder()
                .memberId(account.getMemberId())
                .bankCode(account.getBankCode())
                .bankAccountNo(account.getBankAccountNo())
                .valid(account.isValid())
                .createdAt(LocalDateTime.now())
                .build();
        BankAccountEntity saved = bankAccountRepository.save(entity);
        // history 기록
        bankAccountHistoryRepository.save(BankAccountHistoryEntity.builder()
                .bankAccountId(saved.getId())
                .action("REGISTERED")
                .createdAt(LocalDateTime.now())
                .build());
        return toDomain(saved);
    }

    @Override
    public BankAccount update(BankAccount account) {
        BankAccountEntity entity = bankAccountRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("BankAccount not found: " + account.getId()));
        entity.setMemberId(account.getMemberId());
        entity.setBankCode(account.getBankCode());
        entity.setBankAccountNo(account.getBankAccountNo());
        entity.setValid(account.isValid());
        entity.setUpdatedAt(LocalDateTime.now());
        BankAccountEntity saved = bankAccountRepository.save(entity);
        bankAccountHistoryRepository.save(BankAccountHistoryEntity.builder()
                .bankAccountId(saved.getId())
                .action("UPDATED")
                .createdAt(LocalDateTime.now())
                .build());
        return toDomain(saved);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!bankAccountRepository.existsById(id)) return false;
        bankAccountRepository.deleteById(id);
        bankAccountHistoryRepository.save(BankAccountHistoryEntity.builder()
                .bankAccountId(id)
                .action("DELETED")
                .createdAt(LocalDateTime.now())
                .build());
        return true;
    }

    // ---- BankAccount (Query)
    @Override
    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<BankAccount> search(Long memberId, String bankCode, String bankAccountNo, Boolean valid, LocalDateTime createdFrom, LocalDateTime createdTo) {
        return bankAccountRepository.search(memberId, bankCode, bankAccountNo, valid, createdFrom, createdTo)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BankAccount toDomain(BankAccountEntity e) {
        return BankAccount.builder()
                .id(e.getId())
                .memberId(e.getMemberId())
                .bankCode(e.getBankCode())
                .bankAccountNo(e.getBankAccountNo())
                .valid(e.isValid())
                .build();
    }

    // ---- History (Query)
    @Override
    public Optional<BankAccountHistory> findHistoryById(Long id) {
        return bankAccountHistoryRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<BankAccountHistory> search(Long bankAccountId, String action, LocalDateTime from, LocalDateTime to) {
        return bankAccountHistoryRepository.search(bankAccountId, action, from, to)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private BankAccountHistory toDomain(BankAccountHistoryEntity e) {
        return BankAccountHistory.builder()
                .id(e.getId())
                .registeredBankAccountId(e.getBankAccountId())
                .action(e.getAction())
                .build();
    }
}
