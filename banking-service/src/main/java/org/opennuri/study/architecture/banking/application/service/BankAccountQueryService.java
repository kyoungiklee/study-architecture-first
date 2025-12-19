package org.opennuri.study.architecture.banking.application.service;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.banking.application.port.in.query.GetBankAccountQuery;
import org.opennuri.study.architecture.banking.application.port.in.query.SearchBankAccountQuery;
import org.opennuri.study.architecture.banking.application.port.out.QueryBankAccountHistoryPort;
import org.opennuri.study.architecture.banking.application.port.out.QueryBankAccountPort;
import org.opennuri.study.architecture.banking.domain.BankAccount;
import org.opennuri.study.architecture.banking.domain.BankAccountHistory;
import org.opennuri.study.archtecture.common.UseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 은행 계좌 관련 조회(단건 조회, 검색, 이력 조회)를 처리하는 애플리케이션 서비스입니다.
 */
@UseCase
@RequiredArgsConstructor
public class BankAccountQueryService implements GetBankAccountQuery, SearchBankAccountQuery {

    private final QueryBankAccountPort queryBankAccountPort;
    private final QueryBankAccountHistoryPort queryBankAccountHistoryPort;

    /**
     * ID로 은행 계좌를 조회합니다.
     *
     * @param id 계좌 ID
     * @return 계좌 도메인 모델 (Optional)
     */
    @Override
    public Optional<BankAccount> getById(Long id) {
        return queryBankAccountPort.findById(id);
    }

    /**
     * 조건에 맞는 은행 계좌 목록을 검색합니다.
     *
     * @param memberId 회원 ID
     * @param bankCode 은행 코드
     * @param bankAccountNo 계좌 번호
     * @param valid 유효 상태
     * @param createdFrom 생성일 시작 범위
     * @param createdTo 생성일 종료 범위
     * @return 검색된 계좌 목록
     */
    @Override
    public List<BankAccount> search(Long memberId, String bankCode, String bankAccountNo, Boolean valid, LocalDateTime createdFrom, LocalDateTime createdTo) {
        return queryBankAccountPort.search(memberId, bankCode, bankAccountNo, valid, createdFrom, createdTo);
    }

    /**
     * ID로 계좌 변경 이력을 조회합니다.
     *
     * @param id 이력 ID
     * @return 계좌 이력 도메인 모델 (Optional)
     */
    public Optional<BankAccountHistory> getHistoryById(Long id) {
        return queryBankAccountHistoryPort.findHistoryById(id);
    }

    /**
     * 특정 계좌의 변경 이력을 검색합니다.
     *
     * @param bankAccountId 계좌 ID
     * @param action 수행된 작업
     * @param from 시작일 범위
     * @param to 종료일 범위
     * @return 검색된 계좌 이력 목록
     */
    public List<BankAccountHistory> searchHistories(Long bankAccountId, String action, LocalDateTime from, LocalDateTime to) {
        return queryBankAccountHistoryPort.search(bankAccountId, action, from, to);
    }
}
