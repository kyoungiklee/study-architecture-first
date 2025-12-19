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

/**
 * 은행 계좌 및 이력에 대한 영속성 처리를 담당하는 어댑터입니다.
 * 아웃바운드 포트를 구현하며 JPA 리포지토리를 사용하여 DB와 상호작용합니다.
 */
@PersistanceAdapter
@RequiredArgsConstructor
public class BankingPersistenceAdapter implements CommandBankAccountPort,
        QueryBankAccountPort,
        QueryBankAccountHistoryPort {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountHistoryRepository bankAccountHistoryRepository;

    /**
     * 새로운 은행 계좌를 생성하고 등록 이력을 남깁니다.
     *
     * @param account 저장할 계좌 도메인 모델
     * @return 저장된 계좌 도메인 모델
     */
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

    /**
     * 계좌 정보를 수정하고 수정 이력을 남깁니다.
     *
     * @param account 수정할 정보가 담긴 도메인 모델
     * @return 업데이트된 도메인 모델
     */
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

    /**
     * 계좌를 삭제하고 삭제 이력을 남깁니다.
     *
     * @param id 삭제할 계좌 ID
     * @return 삭제 성공 시 true
     */
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

    /**
     * ID로 은행 계좌를 조회합니다.
     *
     * @param id 계좌 ID
     * @return 조회된 계좌 도메인 모델 (Optional)
     */
    @Override
    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id).map(this::toDomain);
    }

    /**
     * 조건에 맞는 계좌 목록을 검색합니다.
     *
     * @param memberId 회원 ID
     * @param bankCode 은행 코드
     * @param bankAccountNo 계좌 번호
     * @param valid 유효 여부
     * @param createdFrom 생성일 시작 범위
     * @param createdTo 생성일 종료 범위
     * @return 검색된 계좌 목록
     */
    @Override
    public List<BankAccount> search(Long memberId, String bankCode, String bankAccountNo, Boolean valid, LocalDateTime createdFrom, LocalDateTime createdTo) {
        return bankAccountRepository.search(memberId, bankCode, bankAccountNo, valid, createdFrom, createdTo)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * 엔티티를 도메인 모델로 변환합니다.
     */
    private BankAccount toDomain(BankAccountEntity e) {
        return BankAccount.builder()
                .id(e.getId())
                .memberId(e.getMemberId())
                .bankCode(e.getBankCode())
                .bankAccountNo(e.getBankAccountNo())
                .valid(e.isValid())
                .build();
    }

    /**
     * ID로 변경 이력을 조회합니다.
     *
     * @param id 이력 ID
     * @return 조회된 이력 도메인 모델 (Optional)
     */
    @Override
    public Optional<BankAccountHistory> findHistoryById(Long id) {
        return bankAccountHistoryRepository.findById(id).map(this::toDomain);
    }

    /**
     * 조건에 맞는 변경 이력 목록을 검색합니다.
     *
     * @param bankAccountId 계좌 ID
     * @param action 작업 구분
     * @param from 시작일 범위
     * @param to 종료일 범위
     * @return 검색된 이력 목록
     */
    @Override
    public List<BankAccountHistory> search(Long bankAccountId, String action, LocalDateTime from, LocalDateTime to) {
        return bankAccountHistoryRepository.search(bankAccountId, action, from, to)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    /**
     * 이력 엔티티를 도메인 모델로 변환합니다.
     */
    private BankAccountHistory toDomain(BankAccountHistoryEntity e) {
        return BankAccountHistory.builder()
                .id(e.getId())
                .registeredBankAccountId(e.getBankAccountId())
                .action(e.getAction())
                .build();
    }
}
